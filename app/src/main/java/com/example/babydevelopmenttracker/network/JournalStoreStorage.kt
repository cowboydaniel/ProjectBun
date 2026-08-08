package com.example.babydevelopmenttracker.network

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.File

private const val STORAGE_TAG = "JournalStoreStorage"

/**
 * Persistence for the family journal store the host is authoritative for.
 *
 * The store used to live only in memory, so the host losing its process discarded every shared
 * entry and a partner joining afterwards synced an empty journal.
 */
interface JournalStoreStorage {
    suspend fun load(): Map<String, Map<String, JournalEntryPayload>>

    suspend fun save(store: Map<String, Map<String, JournalEntryPayload>>)

    /** Keeps nothing. Used by tests, which do not want a file on disk. */
    object None : JournalStoreStorage {
        override suspend fun load(): Map<String, Map<String, JournalEntryPayload>> = emptyMap()

        override suspend fun save(store: Map<String, Map<String, JournalEntryPayload>>) = Unit
    }
}

/**
 * Stores the journal as JSON in the app's private files directory.
 *
 * Writes go to a temporary file that is then renamed over the target, so a process death partway
 * through a write leaves the previous contents intact rather than a truncated file.
 */
class FileJournalStoreStorage(
    private val file: File,
    moshi: Moshi,
) : JournalStoreStorage {

    private val adapter = moshi.adapter<Map<String, Map<String, JournalEntryPayload>>>(
        Types.newParameterizedType(
            Map::class.java,
            String::class.java,
            Types.newParameterizedType(
                Map::class.java,
                String::class.java,
                JournalEntryPayload::class.java
            )
        )
    )

    override suspend fun load(): Map<String, Map<String, JournalEntryPayload>> {
        if (!file.exists()) return emptyMap()
        return runCatching { adapter.fromJson(file.readText()) }
            .onFailure { error -> Log.w(STORAGE_TAG, "Failed to read journal store", error) }
            .getOrNull()
            .orEmpty()
    }

    override suspend fun save(store: Map<String, Map<String, JournalEntryPayload>>) {
        runCatching {
            file.parentFile?.mkdirs()
            val temporaryFile = File(file.parentFile, "${file.name}.tmp")
            temporaryFile.writeText(adapter.toJson(store))
            if (!temporaryFile.renameTo(file)) {
                file.writeText(temporaryFile.readText())
                temporaryFile.delete()
            }
        }.onFailure { error ->
            Log.w(STORAGE_TAG, "Failed to persist journal store", error)
        }
    }
}
