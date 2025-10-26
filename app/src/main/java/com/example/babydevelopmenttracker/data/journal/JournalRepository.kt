package com.example.babydevelopmenttracker.data.journal

import android.util.Log
import com.example.babydevelopmenttracker.network.FamilySyncGateway
import com.example.babydevelopmenttracker.network.JournalEntryPayload
import java.time.Instant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private const val JOURNAL_REPOSITORY_TAG = "JournalRepository"

class JournalRepository(
    private val journalDao: JournalDao,
    private val familySyncGateway: FamilySyncGateway,
    private val familyIdProvider: suspend () -> String?,
    private val familySecretProvider: suspend () -> String?,
    private val transactionRunner: suspend (suspend () -> Unit) -> Unit,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    val journalEntries: Flow<List<JournalEntry>> =
        journalDao.getEntries().map { entities -> entities.map(JournalEntryEntity::toDomain) }

    suspend fun getEntry(id: String): JournalEntry? = withContext(ioDispatcher) {
        journalDao.getEntryById(id)?.toDomain()
    }

    suspend fun upsertEntry(entry: JournalEntry) {
        withContext(ioDispatcher) {
            journalDao.upsert(entry.toEntity())
            pushRemote(entry)
        }
    }

    suspend fun deleteEntry(entryId: String) {
        withContext(ioDispatcher) {
            journalDao.deleteById(entryId)
            removeRemote(entryId)
        }
    }

    suspend fun refreshFromRemote() {
        withContext(ioDispatcher) {
            val familyId = familyIdProvider() ?: return@withContext
            val secret = familySecretProvider()?.takeUnless(String::isBlank) ?: return@withContext
            runCatching {
                val remoteEntries = familySyncGateway.fetchJournalEntries(
                    familyId,
                    secret,
                )
                transactionRunner {
                    journalDao.clearAll()
                    journalDao.upsert(remoteEntries.map { it.toEntity() })
                }
            }.onFailure { error ->
                Log.w(JOURNAL_REPOSITORY_TAG, "Failed to refresh journal from remote", error)
            }
        }
    }

    private suspend fun pushRemote(entry: JournalEntry) {
        val familyId = familyIdProvider() ?: return
        val secret = familySecretProvider()?.takeUnless(String::isBlank) ?: return
        runCatching {
            familySyncGateway.upsertJournalEntry(
                familyId = familyId,
                secret = secret,
                entry = entry.toPayload(),
            )
        }.onFailure { error ->
            Log.w(JOURNAL_REPOSITORY_TAG, "Failed to push journal entry", error)
        }
    }

    private suspend fun removeRemote(entryId: String) {
        val familyId = familyIdProvider() ?: return
        val secret = familySecretProvider()?.takeUnless(String::isBlank) ?: return
        runCatching {
            familySyncGateway.deleteJournalEntry(
                familyId = familyId,
                secret = secret,
                entryId = entryId,
            )
        }.onFailure { error ->
            Log.w(JOURNAL_REPOSITORY_TAG, "Failed to delete journal entry remotely", error)
        }
    }
}

private fun JournalEntryPayload.toEntity(): JournalEntryEntity =
    JournalEntryEntity(
        id = id,
        timestamp = Instant.ofEpochMilli(timestampEpochMillis),
        mood = JournalMood.valueOf(mood),
        body = body,
        attachments = attachments ?: emptyList(),
    )

private fun JournalEntry.toPayload(): JournalEntryPayload =
    JournalEntryPayload(
        id = id,
        timestampEpochMillis = timestamp.toEpochMilli(),
        mood = mood.name,
        body = body,
        attachments = if (attachments.isEmpty()) null else attachments,
    )
