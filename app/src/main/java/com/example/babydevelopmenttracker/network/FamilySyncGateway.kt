package com.example.babydevelopmenttracker.network

import kotlinx.coroutines.flow.StateFlow

/**
 * Abstraction for exchanging family sync data between devices.
 */
interface FamilySyncGateway {
    val connectionState: StateFlow<PeerConnectionState>

    suspend fun createFamily(
        familyId: String,
        request: CreateFamilyRequest,
    ): FamilyRegistrationResponse

    suspend fun registerFamilyMember(
        familyId: String,
        request: RegisterFamilyMemberRequest,
    ): FamilyRegistrationResponse

    suspend fun fetchJournalEntries(
        familyId: String,
        secret: String,
    ): List<JournalEntryPayload>

    suspend fun upsertJournalEntry(
        familyId: String,
        secret: String,
        entry: JournalEntryPayload,
    )

    suspend fun deleteJournalEntry(
        familyId: String,
        secret: String,
        entryId: String,
    )

    fun startAdvertising(endpointName: String)

    fun stopAdvertising()

    fun startDiscovery(endpointName: String)

    fun stopDiscovery()

    fun connectToEndpoint(endpointId: String)

    fun disconnectEndpoint(endpointId: String)

    fun shutdown()
}

data class CreateFamilyRequest(val secret: String? = null)

data class RegisterFamilyMemberRequest(
    val inviteCode: String,
    val deviceIdentifier: String? = null,
)

data class FamilyRegistrationResponse(val authToken: String)

data class JournalEntryPayload(
    val id: String,
    val timestampEpochMillis: Long,
    val mood: String,
    val body: String,
    val attachments: List<String>? = null,
)

data class PeerConnectionState(
    val advertising: Boolean = false,
    val discovering: Boolean = false,
    val discoveredEndpoints: List<PeerEndpoint> = emptyList(),
    val connectedEndpoints: List<PeerEndpoint> = emptyList(),
)

data class PeerEndpoint(val id: String, val name: String)
