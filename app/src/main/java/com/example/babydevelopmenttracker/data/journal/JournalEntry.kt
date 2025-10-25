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
)

data class JournalEntry(
    val id: String,
    val timestamp: Instant,
    val mood: JournalMood,
    val body: String,
    val attachments: List<String> = emptyList(),
)

fun JournalEntryEntity.toDomain(): JournalEntry =
    JournalEntry(
        id = id,
        timestamp = timestamp,
        mood = mood,
        body = body,
        attachments = attachments,
    )

fun JournalEntry.toEntity(): JournalEntryEntity =
    JournalEntryEntity(
        id = id,
        timestamp = timestamp,
        mood = mood,
        body = body,
        attachments = attachments,
    )
