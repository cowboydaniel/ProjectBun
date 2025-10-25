package com.example.babydevelopmenttracker.journal

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.example.babydevelopmenttracker.R
import com.example.babydevelopmenttracker.data.journal.JournalEntry
import com.example.babydevelopmenttracker.data.journal.JournalMood
import com.example.babydevelopmenttracker.ui.journal.JournalListScreen
import com.example.babydevelopmenttracker.ui.theme.BabyDevelopmentTrackerTheme
import java.time.Instant
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class JournalScreensTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun expectantParentListShowsEntries() {
        val entry = JournalEntry(
            id = "entry-1",
            timestamp = Instant.ofEpochMilli(1_000L),
            mood = JournalMood.JOYFUL,
            body = "Feeling excited today",
            attachments = emptyList()
        )

        composeRule.setContent {
            BabyDevelopmentTrackerTheme {
                JournalListScreen(
                    entries = listOf(entry),
                    onAddEntry = {},
                    onEntrySelected = {},
                    onExport = {},
                    isPartnerView = false,
                    canPartnerViewEntries = true
                )
            }
        }

        composeRule.onNodeWithText(entry.body).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(
            composeRule.activity.getString(R.string.journal_add_entry)
        ).assertIsDisplayed()
    }

    @Test
    fun partnerBlockedSeesSharingMessage() {
        composeRule.setContent {
            BabyDevelopmentTrackerTheme {
                JournalListScreen(
                    entries = emptyList(),
                    onAddEntry = {},
                    onEntrySelected = {},
                    onExport = {},
                    isPartnerView = true,
                    canPartnerViewEntries = false
                )
            }
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.journal_partner_blocked_title)
        ).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(
            composeRule.activity.getString(R.string.journal_add_entry)
        ).assertDoesNotExist()
    }

    @Test
    fun partnerWithSharingEnabledCanStartEntries() {
        val entry = JournalEntry(
            id = "entry-2",
            timestamp = Instant.ofEpochMilli(5_000L),
            mood = JournalMood.CALM,
            body = "Staying centered",
            attachments = emptyList()
        )

        composeRule.setContent {
            BabyDevelopmentTrackerTheme {
                JournalListScreen(
                    entries = listOf(entry),
                    onAddEntry = {},
                    onEntrySelected = {},
                    onExport = {},
                    isPartnerView = true,
                    canPartnerViewEntries = true
                )
            }
        }

        composeRule.onNodeWithText(entry.body).assertIsDisplayed()
        composeRule.onNodeWithContentDescription(
            composeRule.activity.getString(R.string.journal_add_entry)
        ).assertIsDisplayed()
    }
}
