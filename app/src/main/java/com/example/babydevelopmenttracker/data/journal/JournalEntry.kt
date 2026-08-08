package com.example.babydevelopmenttracker.data.journal

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val timestamp: Instant,
    val mood: JournalMood,
    val body: String,
    val attachments: List<String>,
    /** Private entries stay on this device and are never sent to the family. */
    val isPrivate: Boolean = false,
)

data class JournalEntry(
    val id: String,
    val timestamp: Instant,
    val mood: JournalMood,
    val body: String,
    val attachments: List<String> = emptyList(),
    val isPrivate: Boolean = false,
)

fun JournalEntryEntity.toDomain(): JournalEntry =
    JournalEntry(
        id = id,
        timestamp = timestamp,
        mood = mood,
        body = body,
        attachments = attachments,
        isPrivate = isPrivate,
    )

fun JournalEntry.toEntity(): JournalEntryEntity =
    JournalEntryEntity(
        id = id,
        timestamp = timestamp,
        mood = mood,
        body = body,
        attachments = attachments,
        isPrivate = isPrivate,
    )
