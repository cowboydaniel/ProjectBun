package com.example.babydevelopmenttracker.data

import com.example.babydevelopmenttracker.data.journal.JournalDao
import com.example.babydevelopmenttracker.data.journal.JournalEntry
import com.example.babydevelopmenttracker.data.journal.JournalEntryEntity
import com.example.babydevelopmenttracker.data.journal.JournalMood
import com.example.babydevelopmenttracker.data.journal.JournalRepository
import com.example.babydevelopmenttracker.network.CreateFamilyRequest
import com.example.babydevelopmenttracker.network.FamilyRegistrationResponse
import com.example.babydevelopmenttracker.network.FamilySyncGateway
import com.example.babydevelopmenttracker.network.JournalEntryPayload
import com.example.babydevelopmenttracker.network.RegisterFamilyMemberRequest
import com.example.babydevelopmenttracker.network.PeerConnectionState
import java.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class JournalRepositoryTest {

    private lateinit var repository: JournalRepository
    private lateinit var fakeDao: FakeJournalDao
    private lateinit var fakeGateway: FakeFamilySyncGateway
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        fakeDao = FakeJournalDao()
        fakeGateway = FakeFamilySyncGateway()
        repository = JournalRepository(
            journalDao = fakeDao,
            familySyncGateway = fakeGateway,
            familyIdProvider = { "family-123" },
            familySecretProvider = { "token-abc" },
            transactionRunner = { block -> block() },
            ioDispatcher = dispatcher
        )
    }

    @Test
    fun refreshFromRemote_replacesLocalEntries() = runTest(dispatcher) {
        val payload = JournalEntryPayload(
            id = "entry-1",
            timestampEpochMillis = 1_000L,
            mood = JournalMood.JOYFUL.name,
            body = "Shared update",
            attachments = listOf("photo.jpg")
        )
        fakeGateway.remoteEntries.add(payload)

        repository.refreshFromRemote()
        advanceUntilIdle()

        val entries = repository.journalEntries.first()
        assertEquals(1, entries.size)
        val entry = entries.first()
        assertEquals(payload.id, entry.id)
        assertEquals(payload.body, entry.body)
        assertEquals(JournalMood.JOYFUL, entry.mood)
        assertEquals(payload.attachments, entry.attachments)
    }

    @Test
    fun upsertEntry_pushesChangesToRemote() = runTest(dispatcher) {
        val entry = JournalEntry(
            id = "entry-42",
            timestamp = Instant.ofEpochMilli(5_000L),
            mood = JournalMood.CALM,
            body = "Feeling grounded today",
            attachments = listOf("note.txt")
        )

        repository.upsertEntry(entry)
        advanceUntilIdle()

        assertEquals(1, fakeGateway.remoteEntries.size)
        val payload = fakeGateway.remoteEntries.first()
        assertEquals(entry.id, payload.id)
        assertEquals(entry.body, payload.body)
        assertEquals(entry.mood.name, payload.mood)
        assertEquals(entry.attachments, payload.attachments)

        val stored = repository.journalEntries.first().first()
        assertEquals(entry.id, stored.id)
        assertEquals(entry.body, stored.body)
    }

    private class FakeJournalDao : JournalDao {
        private val entries = mutableListOf<JournalEntryEntity>()
        private val entriesFlow = MutableStateFlow<List<JournalEntryEntity>>(emptyList())

        override fun getEntries(): Flow<List<JournalEntryEntity>> = entriesFlow

        override suspend fun getEntryById(id: String): JournalEntryEntity? =
            entries.firstOrNull { it.id == id }

        override suspend fun upsert(entry: JournalEntryEntity) {
            entries.removeAll { it.id == entry.id }
            entries.add(entry)
            emit()
        }

        override suspend fun upsert(entries: List<JournalEntryEntity>) {
            this.entries.clear()
            this.entries.addAll(entries)
            emit()
        }

        override suspend fun deleteById(id: String) {
            entries.removeAll { it.id == id }
            emit()
        }

        override suspend fun clearAll() {
            entries.clear()
            emit()
        }

        private fun emit() {
            entriesFlow.value = entries.sortedByDescending { it.timestamp }
        }
    }

    private class FakeFamilySyncGateway : FamilySyncGateway {
        val remoteEntries = mutableListOf<JournalEntryPayload>()

        override val connectionState = MutableStateFlow(PeerConnectionState())

        override suspend fun createFamily(
            familyId: String,
            request: CreateFamilyRequest
        ): FamilyRegistrationResponse {
            return FamilyRegistrationResponse(request.secret ?: "secret")
        }

        override suspend fun registerFamilyMember(
            familyId: String,
            request: RegisterFamilyMemberRequest
        ): FamilyRegistrationResponse {
            return FamilyRegistrationResponse("token-abc")
        }

        override suspend fun fetchJournalEntries(
            familyId: String,
            secret: String,
        ): List<JournalEntryPayload> = remoteEntries.toList()

        override suspend fun upsertJournalEntry(
            familyId: String,
            secret: String,
            entry: JournalEntryPayload,
        ) {
            remoteEntries.removeAll { it.id == entry.id }
            remoteEntries.add(entry)
        }

        override suspend fun deleteJournalEntry(
            familyId: String,
            secret: String,
            entryId: String,
        ) {
            remoteEntries.removeAll { it.id == entryId }
        }

        override fun startAdvertising(endpointName: String) {}

        override fun stopAdvertising() {}

        override fun startDiscovery(endpointName: String) {}

        override fun stopDiscovery() {}

        override fun connectToEndpoint(endpointId: String) {}

        override fun disconnectEndpoint(endpointId: String) {}

        override fun shutdown() {}
    }
}
