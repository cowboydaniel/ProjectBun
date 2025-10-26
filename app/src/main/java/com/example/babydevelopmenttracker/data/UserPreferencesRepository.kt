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
import kotlinx.coroutines.flow.firstOrNull
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
    val DUE_DATE_FROM_PARTNER = booleanPreferencesKey("due_date_from_partner")
    val PARTNER_LINK_APPROVED = booleanPreferencesKey("partner_link_approved")
    val SHARE_JOURNAL_WITH_PARTNER = booleanPreferencesKey("share_journal_with_partner")
    val FAMILY_LINK_ID = stringPreferencesKey("family_link_id")
    val FAMILY_LINK_SECRET = stringPreferencesKey("family_link_secret")
}

data class UserPreferences(
    val dueDateEpochDay: Long? = null,
    val remindersEnabled: Boolean = false,
    val familyRole: FamilyRole? = null,
    val onboardingCompleted: Boolean = false,
    val themePreference: ThemePreference = ThemePreference.Neutral,
    val dueDateFromPartnerInvite: Boolean = false,
    val partnerLinkApproved: Boolean = false,
    val shareJournalWithPartner: Boolean = false,
    val familyLinkId: String? = null,
    val familyLinkSecret: String? = null,
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
                ),
                dueDateFromPartnerInvite =
                    preferences[UserPreferencesKeys.DUE_DATE_FROM_PARTNER] ?: false,
                partnerLinkApproved = preferences[UserPreferencesKeys.PARTNER_LINK_APPROVED] ?: false,
                shareJournalWithPartner =
                    preferences[UserPreferencesKeys.SHARE_JOURNAL_WITH_PARTNER] ?: false,
                familyLinkId = preferences[UserPreferencesKeys.FAMILY_LINK_ID],
                familyLinkSecret = preferences[UserPreferencesKeys.FAMILY_LINK_SECRET],
            )
        }

    suspend fun updateReminderEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[UserPreferencesKeys.REMINDER_ENABLED] = enabled
        }
    }

    suspend fun updateDueDate(epochDay: Long?, fromPartnerInvite: Boolean = false) {
        context.userPreferencesDataStore.edit { preferences ->
            if (epochDay == null) {
                preferences.remove(UserPreferencesKeys.DUE_DATE_EPOCH_DAY)
                preferences[UserPreferencesKeys.DUE_DATE_FROM_PARTNER] = false
            } else {
                preferences[UserPreferencesKeys.DUE_DATE_EPOCH_DAY] = epochDay
                preferences[UserPreferencesKeys.DUE_DATE_FROM_PARTNER] = fromPartnerInvite
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

    suspend fun updatePartnerLinkApproved(approved: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[UserPreferencesKeys.PARTNER_LINK_APPROVED] = approved
        }
    }

    suspend fun updateShareJournalWithPartner(share: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[UserPreferencesKeys.SHARE_JOURNAL_WITH_PARTNER] = share
        }
    }

    suspend fun updateFamilyLink(familyId: String?, familySecret: String?) {
        context.userPreferencesDataStore.edit { preferences ->
            if (familyId.isNullOrEmpty()) {
                preferences.remove(UserPreferencesKeys.FAMILY_LINK_ID)
            } else {
                preferences[UserPreferencesKeys.FAMILY_LINK_ID] = familyId
            }

            if (familySecret.isNullOrEmpty()) {
                preferences.remove(UserPreferencesKeys.FAMILY_LINK_SECRET)
            } else {
                preferences[UserPreferencesKeys.FAMILY_LINK_SECRET] = familySecret
            }
        }
    }

}
