package com.example.babydevelopmenttracker.data.journal

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.time.Instant

class JournalTypeConverters {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val attachmentsAdapter =
        moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))

    @TypeConverter
    fun fromInstant(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun fromMood(mood: JournalMood?): String? = mood?.name

    @TypeConverter
    fun toMood(value: String?): JournalMood? = value?.let { JournalMood.valueOf(it) }

    @TypeConverter
    fun fromAttachments(attachments: List<String>?): String? =
        attachments?.let { attachmentsAdapter.toJson(it) }

    @TypeConverter
    fun toAttachments(value: String?): List<String> =
        value?.let { attachmentsAdapter.fromJson(it).orEmpty() } ?: emptyList()
}
