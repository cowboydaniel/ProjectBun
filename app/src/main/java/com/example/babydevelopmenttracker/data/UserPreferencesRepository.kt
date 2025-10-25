package com.example.babydevelopmenttracker.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.babydevelopmenttracker.ui.theme.ThemePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

internal object UserPreferencesKeys {
    val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
    val DUE_DATE_EPOCH_DAY = longPreferencesKey("due_date_epoch_day")
    val FAMILY_ROLE = stringPreferencesKey("family_role")
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    val THEME_PREFERENCE = stringPreferencesKey("theme_preference")
}

data class UserPreferences(
    val dueDateEpochDay: Long? = null,
    val remindersEnabled: Boolean = false,
    val familyRole: FamilyRole? = null,
    val onboardingCompleted: Boolean = false,
    val themePreference: ThemePreference = ThemePreference.Neutral
)

class UserPreferencesRepository(private val context: Context) {

    val userPreferences: Flow<UserPreferences> =
        context.userPreferencesDataStore.data.map { preferences ->
            UserPreferences(
                dueDateEpochDay = preferences[UserPreferencesKeys.DUE_DATE_EPOCH_DAY],
                remindersEnabled = preferences[UserPreferencesKeys.REMINDER_ENABLED] ?: false,
                familyRole = FamilyRole.fromStorageValue(preferences[UserPreferencesKeys.FAMILY_ROLE]),
                onboardingCompleted = preferences[UserPreferencesKeys.ONBOARDING_COMPLETED] ?: false,
                themePreference = ThemePreference.fromStorageValue(
                    preferences[UserPreferencesKeys.THEME_PREFERENCE]
                )
            )
        }

    suspend fun updateReminderEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[UserPreferencesKeys.REMINDER_ENABLED] = enabled
        }
    }

    suspend fun updateDueDate(epochDay: Long?) {
        context.userPreferencesDataStore.edit { preferences ->
            if (epochDay == null) {
                preferences.remove(UserPreferencesKeys.DUE_DATE_EPOCH_DAY)
            } else {
                preferences[UserPreferencesKeys.DUE_DATE_EPOCH_DAY] = epochDay
            }
        }
    }

    suspend fun updateFamilyRole(role: FamilyRole?) {
        context.userPreferencesDataStore.edit { preferences ->
            if (role == null) {
                preferences.remove(UserPreferencesKeys.FAMILY_ROLE)
            } else {
                preferences[UserPreferencesKeys.FAMILY_ROLE] = role.storageValue
            }
        }
    }

    suspend fun updateOnboardingCompleted(completed: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[UserPreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun updateThemePreference(themePreference: ThemePreference) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[UserPreferencesKeys.THEME_PREFERENCE] = themePreference.storageValue
        }
    }
}
