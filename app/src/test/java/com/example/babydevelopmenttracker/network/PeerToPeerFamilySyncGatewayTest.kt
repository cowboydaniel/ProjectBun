package com.example.babydevelopmenttracker.network

import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class PeerToPeerFamilySyncGatewayTest {

    @Test
    fun hostUpsertPersistsEntriesLocally() = runTest {
        val gateway = PeerToPeerFamilySyncGateway(FakePeerConnectionClient())
        val familyId = "family-test"
        val secret = "secret-value"
        gateway.createFamily(familyId, CreateFamilyRequest(secret = secret))

        val entry = JournalEntryPayload(
            id = "entry-1",
            timestampEpochMillis = 5_000L,
            mood = "CALM",
            body = "Breathing exercises felt helpful today",
            attachments = listOf("breathwork.mp3"),
        )

        gateway.upsertJournalEntry(familyId, secret, entry)

        val entries = gateway.fetchJournalEntries(familyId, secret)
        assertEquals(1, entries.size)
        val stored = entries.first()
        assertEquals(entry.id, stored.id)
        assertEquals(entry.body, stored.body)
        assertEquals(entry.mood, stored.mood)
        assertEquals(entry.attachments, stored.attachments)
    }

    private class FakePeerConnectionClient : PeerConnectionClient {
        private var listener: PeerConnectionClient.Listener? = null

        override fun setListener(listener: PeerConnectionClient.Listener) {
            this.listener = listener
        }

        override fun startAdvertising(endpointName: String) {}

        override fun stopAdvertising() {}

        override fun startDiscovery(endpointName: String) {}

        override fun stopDiscovery() {}

        override fun requestConnection(endpointId: String, localEndpointName: String) {}

        override fun disconnectEndpoint(endpointId: String) {}

        override fun sendPayload(endpointId: String, payload: ByteArray) {}

        override fun stopAll() {
            listener = null
        }
    }
}
