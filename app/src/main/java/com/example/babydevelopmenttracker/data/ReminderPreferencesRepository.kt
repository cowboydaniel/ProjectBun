package com.example.babydevelopmenttracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.reminderPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

internal object ReminderPreferencesKeys {
    val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
    val DUE_DATE_EPOCH_DAY = longPreferencesKey("due_date_epoch_day")
}

data class ReminderPreferences(
    val dueDateEpochDay: Long? = null,
    val remindersEnabled: Boolean = false
)

class ReminderPreferencesRepository(private val context: Context) {

    val reminderPreferences: Flow<ReminderPreferences> =
        context.reminderPreferencesDataStore.data.map { preferences ->
            ReminderPreferences(
                dueDateEpochDay = preferences[ReminderPreferencesKeys.DUE_DATE_EPOCH_DAY],
                remindersEnabled = preferences[ReminderPreferencesKeys.REMINDER_ENABLED] ?: false
            )
        }

    suspend fun updateReminderEnabled(enabled: Boolean) {
        context.reminderPreferencesDataStore.edit { preferences ->
            preferences[ReminderPreferencesKeys.REMINDER_ENABLED] = enabled
        }
    }

    suspend fun updateDueDate(epochDay: Long?) {
        context.reminderPreferencesDataStore.edit { preferences ->
            if (epochDay == null) {
                preferences.remove(ReminderPreferencesKeys.DUE_DATE_EPOCH_DAY)
            } else {
                preferences[ReminderPreferencesKeys.DUE_DATE_EPOCH_DAY] = epochDay
            }
        }
    }
}
