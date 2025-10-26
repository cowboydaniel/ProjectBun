package com.example.babydevelopmenttracker.data

import com.example.babydevelopmenttracker.data.journal.JournalDao
import com.example.babydevelopmenttracker.data.journal.JournalEntry
import com.example.babydevelopmenttracker.data.journal.JournalEntryEntity
import com.example.babydevelopmenttracker.data.journal.JournalMood
import com.example.babydevelopmenttracker.data.journal.JournalRepository
import com.example.babydevelopmenttracker.network.CreateFamilyRequest
import com.example.babydevelopmenttracker.network.FamilyRegistrationResponse
import com.example.babydevelopmenttracker.network.FamilySyncService
import com.example.babydevelopmenttracker.network.JournalEntryPayload
import com.example.babydevelopmenttracker.network.RegisterFamilyMemberRequest
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
    private lateinit var fakeService: FakeFamilySyncService
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        fakeDao = FakeJournalDao()
        fakeService = FakeFamilySyncService()
        repository = JournalRepository(
            journalDao = fakeDao,
            familySyncService = fakeService,
            familyIdProvider = { "family-123" },
            authTokenProvider = { "token-abc" },
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
        fakeService.remoteEntries.add(payload)

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

        assertEquals(1, fakeService.remoteEntries.size)
        val payload = fakeService.remoteEntries.first()
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

    private class FakeFamilySyncService : FamilySyncService {
        val remoteEntries = mutableListOf<JournalEntryPayload>()

        override suspend fun createFamily(
            familyId: String,
            request: CreateFamilyRequest
        ): FamilyRegistrationResponse {
            throw UnsupportedOperationException("Not implemented in fake")
        }

        override suspend fun registerFamilyMember(
            familyId: String,
            request: RegisterFamilyMemberRequest
        ): FamilyRegistrationResponse {
            throw UnsupportedOperationException("Not implemented in fake")
        }

        override suspend fun fetchJournalEntries(
            familyId: String,
            authorization: String,
        ): List<JournalEntryPayload> =
            remoteEntries.toList()

        override suspend fun upsertJournalEntry(
            familyId: String,
            entryId: String,
            entry: JournalEntryPayload,
            authorization: String,
        ) {
            remoteEntries.removeAll { it.id == entryId }
            remoteEntries.add(entry)
        }

        override suspend fun deleteJournalEntry(
            familyId: String,
            entryId: String,
            authorization: String,
        ) {
            remoteEntries.removeAll { it.id == entryId }
        }
    }
}
