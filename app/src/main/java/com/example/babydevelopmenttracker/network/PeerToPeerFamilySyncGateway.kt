package com.example.babydevelopmenttracker.network

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

private const val TAG = "PeerFamilyGateway"
private const val SERVICE_ID = "com.example.babydevelopmenttracker.sync"
private val REGISTRATION_TIMEOUT = 15.seconds
private val SYNC_TIMEOUT = 10.seconds

class PeerToPeerFamilySyncGateway(
    private val client: PeerConnectionClient,
    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build(),
) : FamilySyncGateway, PeerConnectionClient.Listener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val messageAdapter = moshi.adapter(PeerMessage::class.java)
    private val journalMutex = Mutex()
    private val journalStore = mutableMapOf<String, MutableMap<String, JournalEntryPayload>>()
    private val pendingRegistrations = ConcurrentHashMap<String, CompletableDeferred<FamilyRegistrationResponse>>()
    private val pendingSyncRequests = ConcurrentHashMap<String, CompletableDeferred<List<JournalEntryPayload>>>()
    private val connectedEndpoints = ConcurrentHashMap<String, PeerEndpoint>()
    private val discoveredEndpoints = ConcurrentHashMap<String, PeerEndpoint>()
    @Volatile private var isAdvertising = false
    @Volatile private var isDiscovering = false
    private var localEndpointName: String = ""

    private val membership = ConcurrentHashMap<String, FamilyMembership>()

    private val _connectionState = kotlinx.coroutines.flow.MutableStateFlow(PeerConnectionState())
    override val connectionState: kotlinx.coroutines.flow.StateFlow<PeerConnectionState> = _connectionState

    init {
        client.setListener(this)
    }

    override suspend fun createFamily(
        familyId: String,
        request: CreateFamilyRequest,
    ): FamilyRegistrationResponse {
        val secret = request.secret?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        membership[familyId] = FamilyMembership(secret = secret, isHost = true)
        ensureFamilyStore(familyId)
        return FamilyRegistrationResponse(authToken = secret)
    }

    override suspend fun registerFamilyMember(
        familyId: String,
        request: RegisterFamilyMemberRequest,
    ): FamilyRegistrationResponse {
        val existing = membership[familyId]
        if (existing != null && !existing.isHost) {
            return FamilyRegistrationResponse(existing.secret)
        }
        val deferred = CompletableDeferred<FamilyRegistrationResponse>()
        pendingRegistrations[familyId] = deferred
        val message = PeerMessage(
            type = PeerMessage.Type.REGISTER_REQUEST,
            familyId = familyId,
            inviteCode = request.inviteCode,
            deviceIdentifier = request.deviceIdentifier,
        )
        if (!sendToHost(message)) {
            pendingRegistrations.remove(familyId)
            throw IllegalStateException("No connected host available for registration")
        }
        val response = withTimeout(REGISTRATION_TIMEOUT.inWholeMilliseconds) { deferred.await() }
        membership[familyId] = FamilyMembership(secret = response.authToken, isHost = false)
        ensureFamilyStore(familyId)
        return response
    }

    override suspend fun fetchJournalEntries(
        familyId: String,
        secret: String,
    ): List<JournalEntryPayload> {
        if (membership[familyId]?.secret != secret) {
            membership[familyId] = FamilyMembership(secret = secret, isHost = membership[familyId]?.isHost == true)
        }
        val localEntries = journalMutex.withLock {
            journalStore[familyId]?.values?.sortedByDescending { it.timestampEpochMillis }
        }
        if (!localEntries.isNullOrEmpty()) {
            return localEntries
        }
        val deferred = CompletableDeferred<List<JournalEntryPayload>>()
        pendingSyncRequests[familyId] = deferred
        val message = PeerMessage(
            type = PeerMessage.Type.SYNC_REQUEST,
            familyId = familyId,
            secret = secret,
        )
        if (!sendToHost(message)) {
            pendingSyncRequests.remove(familyId)
            throw IllegalStateException("No connected host available for sync")
        }
        val response = withTimeout(SYNC_TIMEOUT.inWholeMilliseconds) { deferred.await() }
        replaceEntries(familyId, response)
        return response
    }

    override suspend fun upsertJournalEntry(
        familyId: String,
        secret: String,
        entry: JournalEntryPayload,
    ) {
        val membershipInfo = membership[familyId]
        if (membershipInfo?.secret != secret) {
            membership[familyId] = FamilyMembership(secret = secret, isHost = membershipInfo?.isHost == true)
        }
        if (membership[familyId]?.isHost == true) {
            replaceEntry(familyId, entry)
            broadcast(
                PeerMessage(
                    type = PeerMessage.Type.UPSERT_BROADCAST,
                    familyId = familyId,
                    entry = entry,
                )
            )
        } else {
            val message = PeerMessage(
                type = PeerMessage.Type.UPSERT_REQUEST,
                familyId = familyId,
                secret = secret,
                entry = entry,
            )
            sendToHost(message)
        }
    }

    override suspend fun deleteJournalEntry(
        familyId: String,
        secret: String,
        entryId: String,
    ) {
        val membershipInfo = membership[familyId]
        if (membershipInfo?.secret != secret) {
            membership[familyId] = FamilyMembership(secret = secret, isHost = membershipInfo?.isHost == true)
        }
        if (membership[familyId]?.isHost == true) {
            removeEntry(familyId, entryId)
            broadcast(
                PeerMessage(
                    type = PeerMessage.Type.DELETE_BROADCAST,
                    familyId = familyId,
                    entryId = entryId,
                )
            )
        } else {
            val message = PeerMessage(
                type = PeerMessage.Type.DELETE_REQUEST,
                familyId = familyId,
                secret = secret,
                entryId = entryId,
            )
            sendToHost(message)
        }
    }

    override fun startAdvertising(endpointName: String) {
        isAdvertising = true
        localEndpointName = endpointName
        client.startAdvertising(endpointName)
        publishState()
    }

    override fun stopAdvertising() {
        isAdvertising = false
        client.stopAdvertising()
        publishState()
    }

    override fun startDiscovery(endpointName: String) {
        isDiscovering = true
        localEndpointName = endpointName
        discoveredEndpoints.clear()
        client.startDiscovery(endpointName)
        publishState()
    }

    override fun stopDiscovery() {
        isDiscovering = false
        client.stopDiscovery()
        publishState()
    }

    override fun connectToEndpoint(endpointId: String) {
        client.requestConnection(endpointId, localEndpointName)
    }

    override fun disconnectEndpoint(endpointId: String) {
        client.disconnectEndpoint(endpointId)
        connectedEndpoints.remove(endpointId)
        publishState()
    }

    override fun shutdown() {
        client.stopAll()
        scope.cancel()
    }

    override fun onEndpointFound(endpointId: String, name: String) {
        discoveredEndpoints[endpointId] = PeerEndpoint(endpointId, name)
        publishState()
    }

    override fun onEndpointLost(endpointId: String) {
        discoveredEndpoints.remove(endpointId)
        publishState()
    }

    override fun onEndpointConnected(endpointId: String, name: String) {
        connectedEndpoints[endpointId] = PeerEndpoint(endpointId, name)
        discoveredEndpoints.remove(endpointId)
        publishState()
    }

    override fun onEndpointDisconnected(endpointId: String) {
        connectedEndpoints.remove(endpointId)
        publishState()
    }

    override fun onPayloadReceived(endpointId: String, payload: ByteArray) {
        scope.launch {
            runCatching {
                val json = payload.toString(StandardCharsets.UTF_8)
                val message = messageAdapter.fromJson(json)
                if (message != null) {
                    handlePeerMessage(endpointId, message)
                }
            }.onFailure { error ->
                Log.w(TAG, "Failed to parse peer payload", error)
            }
        }
    }

    private suspend fun handlePeerMessage(endpointId: String, message: PeerMessage) {
        when (message.type) {
            PeerMessage.Type.REGISTER_REQUEST -> handleRegisterRequest(endpointId, message)
            PeerMessage.Type.REGISTER_RESPONSE -> handleRegisterResponse(message)
            PeerMessage.Type.SYNC_REQUEST -> handleSyncRequest(endpointId, message)
            PeerMessage.Type.SYNC_RESPONSE -> handleSyncResponse(message)
            PeerMessage.Type.UPSERT_REQUEST -> handleUpsertRequest(endpointId, message)
            PeerMessage.Type.UPSERT_BROADCAST -> handleUpsertBroadcast(message)
            PeerMessage.Type.DELETE_REQUEST -> handleDeleteRequest(endpointId, message)
            PeerMessage.Type.DELETE_BROADCAST -> handleDeleteBroadcast(message)
        }
    }

    private suspend fun handleRegisterRequest(endpointId: String, message: PeerMessage) {
        val hostMembership = membership[message.familyId]
        if (hostMembership?.isHost != true) {
            Log.w(TAG, "Ignoring register request for ${message.familyId} - not host")
            return
        }
        val response = PeerMessage(
            type = PeerMessage.Type.REGISTER_RESPONSE,
            familyId = message.familyId,
            secret = hostMembership.secret,
        )
        sendToEndpoint(endpointId, response)
    }

    private fun handleRegisterResponse(message: PeerMessage) {
        val secret = message.secret ?: return
        pendingRegistrations.remove(message.familyId)?.complete(
            FamilyRegistrationResponse(secret)
        )
    }

    private suspend fun handleSyncRequest(endpointId: String, message: PeerMessage) {
        val hostMembership = membership[message.familyId]
        if (hostMembership?.isHost != true) {
            Log.w(TAG, "Ignoring sync request for ${message.familyId} - not host")
            return
        }
        if (hostMembership.secret != message.secret) {
            Log.w(TAG, "Rejecting sync request: secret mismatch")
            return
        }
        val entries = journalMutex.withLock {
            journalStore[message.familyId]?.values?.sortedByDescending { it.timestampEpochMillis }
                ?: emptyList()
        }
        val response = PeerMessage(
            type = PeerMessage.Type.SYNC_RESPONSE,
            familyId = message.familyId,
            entries = entries,
        )
        sendToEndpoint(endpointId, response)
    }

    private suspend fun handleSyncResponse(message: PeerMessage) {
        val entries = message.entries ?: emptyList()
        replaceEntries(message.familyId, entries)
        pendingSyncRequests.remove(message.familyId)?.complete(entries)
    }

    private suspend fun handleUpsertRequest(endpointId: String, message: PeerMessage) {
        val hostMembership = membership[message.familyId]
        if (hostMembership?.isHost != true) {
            Log.w(TAG, "Ignoring upsert request for ${message.familyId} - not host")
            return
        }
        if (hostMembership.secret != message.secret) {
            Log.w(TAG, "Rejecting upsert request: secret mismatch")
            return
        }
        val entry = message.entry ?: return
        replaceEntry(message.familyId, entry)
        broadcast(
            PeerMessage(
                type = PeerMessage.Type.UPSERT_BROADCAST,
                familyId = message.familyId,
                entry = entry,
            ),
            excludeEndpoint = endpointId,
        )
        sendToEndpoint(endpointId, PeerMessage(
            type = PeerMessage.Type.UPSERT_BROADCAST,
            familyId = message.familyId,
            entry = entry,
        ))
    }

    private suspend fun handleUpsertBroadcast(message: PeerMessage) {
        val entry = message.entry ?: return
        replaceEntry(message.familyId, entry)
    }

    private suspend fun handleDeleteRequest(endpointId: String, message: PeerMessage) {
        val hostMembership = membership[message.familyId]
        if (hostMembership?.isHost != true) {
            Log.w(TAG, "Ignoring delete request for ${message.familyId} - not host")
            return
        }
        if (hostMembership.secret != message.secret) {
            Log.w(TAG, "Rejecting delete request: secret mismatch")
            return
        }
        val entryId = message.entryId ?: return
        removeEntry(message.familyId, entryId)
        broadcast(
            PeerMessage(
                type = PeerMessage.Type.DELETE_BROADCAST,
                familyId = message.familyId,
                entryId = entryId,
            ),
            excludeEndpoint = endpointId,
        )
        sendToEndpoint(endpointId, PeerMessage(
            type = PeerMessage.Type.DELETE_BROADCAST,
            familyId = message.familyId,
            entryId = entryId,
        ))
    }

    private suspend fun handleDeleteBroadcast(message: PeerMessage) {
        val entryId = message.entryId ?: return
        removeEntry(message.familyId, entryId)
    }

    private suspend fun replaceEntry(familyId: String, entry: JournalEntryPayload) {
        journalMutex.withLock {
            val store = ensureFamilyStore(familyId)
            store[entry.id] = entry
        }
    }

    private suspend fun removeEntry(familyId: String, entryId: String) {
        journalMutex.withLock {
            val store = ensureFamilyStore(familyId)
            store.remove(entryId)
        }
    }

    private suspend fun replaceEntries(familyId: String, entries: List<JournalEntryPayload>) {
        journalMutex.withLock {
            val store = ensureFamilyStore(familyId)
            store.clear()
            entries.forEach { entry -> store[entry.id] = entry }
        }
    }

    private fun ensureFamilyStore(familyId: String): MutableMap<String, JournalEntryPayload> {
        return journalStore.getOrPut(familyId) { mutableMapOf() }
    }

    private fun publishState() {
        _connectionState.value = PeerConnectionState(
            advertising = isAdvertising,
            discovering = isDiscovering,
            discoveredEndpoints = discoveredEndpoints.values.sortedBy { it.name },
            connectedEndpoints = connectedEndpoints.values.sortedBy { it.name },
        )
    }

    private fun broadcast(message: PeerMessage, excludeEndpoint: String? = null) {
        val payload = encode(message) ?: return
        connectedEndpoints.keys
            .filter { it != excludeEndpoint }
            .forEach { endpointId ->
                client.sendPayload(endpointId, payload)
            }
    }

    private fun sendToEndpoint(endpointId: String, message: PeerMessage) {
        val payload = encode(message) ?: return
        client.sendPayload(endpointId, payload)
    }

    private fun sendToHost(message: PeerMessage): Boolean {
        val payload = encode(message) ?: return false
        val hostId = connectedEndpoints.keys.firstOrNull() ?: return false
        client.sendPayload(hostId, payload)
        return true
    }

    private fun encode(message: PeerMessage): ByteArray? {
        return runCatching {
            messageAdapter.toJson(message).toByteArray(StandardCharsets.UTF_8)
        }.onFailure { error ->
            Log.w(TAG, "Failed to encode peer message", error)
        }.getOrNull()
    }

    private data class FamilyMembership(
        val secret: String,
        val isHost: Boolean,
    )

    @JsonClass(generateAdapter = true)
    data class PeerMessage(
        val type: Type,
        val familyId: String,
        val secret: String? = null,
        val inviteCode: String? = null,
        val deviceIdentifier: String? = null,
        val entry: JournalEntryPayload? = null,
        val entryId: String? = null,
        val entries: List<JournalEntryPayload>? = null,
    ) {
        enum class Type {
            REGISTER_REQUEST,
            REGISTER_RESPONSE,
            SYNC_REQUEST,
            SYNC_RESPONSE,
            UPSERT_REQUEST,
            UPSERT_BROADCAST,
            DELETE_REQUEST,
            DELETE_BROADCAST,
        }
    }

    companion object {
        fun create(context: Context): PeerToPeerFamilySyncGateway {
            val client = NearbyPeerConnectionClient(context)
            return PeerToPeerFamilySyncGateway(client)
        }
    }
}

interface PeerConnectionClient {
    fun setListener(listener: Listener)
    fun startAdvertising(endpointName: String)
    fun stopAdvertising()
    fun startDiscovery(endpointName: String)
    fun stopDiscovery()
    fun requestConnection(endpointId: String, localEndpointName: String)
    fun disconnectEndpoint(endpointId: String)
    fun sendPayload(endpointId: String, payload: ByteArray)
    fun stopAll()

    interface Listener {
        fun onEndpointFound(endpointId: String, name: String)
        fun onEndpointLost(endpointId: String)
        fun onEndpointConnected(endpointId: String, name: String)
        fun onEndpointDisconnected(endpointId: String)
        fun onPayloadReceived(endpointId: String, payload: ByteArray)
    }
}

private class NearbyPeerConnectionClient(
    context: Context,
    private val serviceId: String = SERVICE_ID,
) : PeerConnectionClient {

    private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(context)
    private var listener: PeerConnectionClient.Listener? = null
    private val endpointNames = ConcurrentHashMap<String, String>()

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val bytes = payload.asBytes() ?: return
            listener?.onPayloadReceived(endpointId, bytes)
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // no-op
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: com.google.android.gms.nearby.connection.ConnectionInfo) {
            endpointNames[endpointId] = info.endpointName
            connectionsClient.acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                val name = endpointNames[endpointId] ?: endpointId
                listener?.onEndpointConnected(endpointId, name)
            } else {
                connectionsClient.disconnectFromEndpoint(endpointId)
                endpointNames.remove(endpointId)
            }
        }

        override fun onDisconnected(endpointId: String) {
            endpointNames.remove(endpointId)
            listener?.onEndpointDisconnected(endpointId)
        }
    }

    private val discoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            endpointNames[endpointId] = info.endpointName
            listener?.onEndpointFound(endpointId, info.endpointName)
        }

        override fun onEndpointLost(endpointId: String) {
            endpointNames.remove(endpointId)
            listener?.onEndpointLost(endpointId)
        }
    }

    override fun setListener(listener: PeerConnectionClient.Listener) {
        this.listener = listener
    }

    override fun startAdvertising(endpointName: String) {
        val options = AdvertisingOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()
        connectionsClient.startAdvertising(
            endpointName,
            serviceId,
            connectionLifecycleCallback,
            options,
        )
    }

    override fun stopAdvertising() {
        connectionsClient.stopAdvertising()
    }

    override fun startDiscovery(endpointName: String) {
        val options = com.google.android.gms.nearby.connection.DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()
        connectionsClient.startDiscovery(serviceId, discoveryCallback, options)
    }

    override fun stopDiscovery() {
        connectionsClient.stopDiscovery()
    }

    override fun requestConnection(endpointId: String, localEndpointName: String) {
        connectionsClient.requestConnection(localEndpointName, endpointId, connectionLifecycleCallback)
    }

    override fun disconnectEndpoint(endpointId: String) {
        connectionsClient.disconnectFromEndpoint(endpointId)
        endpointNames.remove(endpointId)
    }

    override fun sendPayload(endpointId: String, payload: ByteArray) {
        connectionsClient.sendPayload(endpointId, Payload.fromBytes(payload))
    }

    override fun stopAll() {
        connectionsClient.stopAdvertising()
        connectionsClient.stopDiscovery()
        connectionsClient.stopAllEndpoints()
        endpointNames.clear()
    }
}
