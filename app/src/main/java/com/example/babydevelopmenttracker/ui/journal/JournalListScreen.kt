package com.example.babydevelopmenttracker.ui.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.babydevelopmenttracker.R
import com.example.babydevelopmenttracker.data.journal.JournalEntry
import com.example.babydevelopmenttracker.data.journal.JournalMood
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun JournalListScreen(
    entries: List<JournalEntry>,
    onAddEntry: () -> Unit,
    onEntrySelected: (JournalEntry) -> Unit,
    onExport: () -> Unit,
    isPartnerView: Boolean,
    canPartnerViewEntries: Boolean,
    modifier: Modifier = Modifier,
    zoneId: ZoneId = ZoneId.systemDefault(),
) {
    val formatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.getDefault()) }
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.journal_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.weight(1f))
                FilledTonalButton(
                    onClick = onExport,
                    enabled = entries.isNotEmpty() && (!isPartnerView || canPartnerViewEntries),
                    colors = ButtonDefaults.filledTonalButtonColors(),
                ) {
                    Icon(
                        imageVector = Icons.Filled.PictureAsPdf,
                        contentDescription = stringResource(id = R.string.journal_export_action)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(id = R.string.journal_export_action))
                }
            }

            when {
                isPartnerView && !canPartnerViewEntries -> {
                    JournalEmptyState(
                        title = stringResource(id = R.string.journal_partner_blocked_title),
                        body = stringResource(id = R.string.journal_partner_blocked_body)
                    )
                }

                entries.isEmpty() -> {
                    JournalEmptyState(
                        title = stringResource(id = R.string.journal_empty_title),
                        body = stringResource(id = R.string.journal_empty_body)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(entries) { entry ->
                            JournalEntryCard(
                                entry = entry,
                                formattedTimestamp = formatter.format(entry.timestamp.atZone(zoneId)),
                                onClick = { onEntrySelected(entry) }
                            )
                        }
                    }
                }
            }
        }

        val canCreateEntry = !isPartnerView || canPartnerViewEntries
        if (canCreateEntry) {
            FloatingActionButton(
                onClick = onAddEntry,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.journal_add_entry)
                )
            }
        }
    }
}

@Composable
private fun JournalEntryCard(
    entry: JournalEntry,
    formattedTimestamp: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = formattedTimestamp,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(
                    id = R.string.journal_mood_label,
                    moodDisplayName(entry.mood)
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = entry.body,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3
            )
        }
    }
}

@Composable
private fun JournalEmptyState(title: String, body: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun moodDisplayName(mood: JournalMood): String {
    return when (mood) {
        JournalMood.JOYFUL -> stringResource(id = R.string.journal_mood_joyful)
        JournalMood.GRATEFUL -> stringResource(id = R.string.journal_mood_grateful)
        JournalMood.TIRED -> stringResource(id = R.string.journal_mood_tired)
        JournalMood.CALM -> stringResource(id = R.string.journal_mood_calm)
        JournalMood.ANXIOUS -> stringResource(id = R.string.journal_mood_anxious)
    }
}
