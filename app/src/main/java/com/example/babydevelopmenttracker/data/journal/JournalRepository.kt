package com.example.babydevelopmenttracker.data.journal

import android.util.Log
import com.example.babydevelopmenttracker.network.FamilySyncGateway
import com.example.babydevelopmenttracker.network.JournalEntryPayload
import com.example.babydevelopmenttracker.network.JournalUpdate
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

    /**
     * Applies journal changes arriving from other devices to the local database.
     *
     * Never returns; collect it from a scope tied to the screen's lifetime. Without it the gateway
     * received changes into its own store and nothing else ever saw them.
     */
    suspend fun observeRemoteUpdates() {
        familySyncGateway.journalUpdates.collect { update ->
            val familyId = familyIdProvider()
            // Ignore traffic for a family this device is not part of.
            if (familyId == null || familyId != update.familyId) {
                return@collect
            }
            withContext(ioDispatcher) {
                runCatching {
                    when (update) {
                        is JournalUpdate.Upserted -> journalDao.upsert(update.entry.toEntity())
                        is JournalUpdate.Deleted -> journalDao.deleteById(update.entryId)
                    }
                }.onFailure { error ->
                    Log.w(JOURNAL_REPOSITORY_TAG, "Failed to apply remote journal update", error)
                }
            }
        }
    }

    /**
     * Pushes every local entry into the shared store.
     *
     * A host that already kept a journal before linking had none of it in the shared store, since
     * entries are only pushed as they are written. Without this the partner joined to an empty
     * journal and only ever saw entries written after the link was made.
     */
    suspend fun publishAllToRemote() {
        withContext(ioDispatcher) {
            familyIdProvider() ?: return@withContext
            familySecretProvider()?.takeUnless(String::isBlank) ?: return@withContext
            val entries = runCatching { journalDao.getAllEntries() }
                .onFailure { error ->
                    Log.w(JOURNAL_REPOSITORY_TAG, "Failed to read entries to publish", error)
                }
                .getOrNull()
                .orEmpty()
            entries.filterNot(JournalEntryEntity::isPrivate)
                .forEach { entity -> pushRemote(entity.toDomain()) }
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
                    // Only shared entries are replaced by the family's copy. Clearing everything
                    // would destroy this device's private entries, which the family never holds.
                    journalDao.clearShared()
                    journalDao.upsert(remoteEntries.map { it.toEntity() })
                }
            }.onFailure { error ->
                Log.w(JOURNAL_REPOSITORY_TAG, "Failed to refresh journal from remote", error)
            }
        }
    }

    private suspend fun pushRemote(entry: JournalEntry) {
        if (entry.isPrivate) {
            // An entry can be made private after it was shared, so withdraw it rather than
            // leaving the previously published copy in the family store.
            removeRemote(entry.id)
            return
        }
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
