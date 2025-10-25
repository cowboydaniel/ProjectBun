package com.example.babydevelopmenttracker.ui.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.babydevelopmenttracker.R
import com.example.babydevelopmenttracker.data.journal.JournalEntry
import com.example.babydevelopmenttracker.data.journal.JournalMood
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@Composable
fun JournalEditorScreen(
    initialEntry: JournalEntry?,
    onSave: (JournalEntry) -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)? = null,
    isSaving: Boolean,
    modifier: Modifier = Modifier,
    zoneId: ZoneId = ZoneId.systemDefault(),
) {
    val formatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a", Locale.getDefault()) }
    val existingId = initialEntry?.id
    val existingTimestamp = initialEntry?.timestamp ?: Instant.now()
    var mood by rememberSaveable { mutableStateOf(initialEntry?.mood ?: JournalMood.CALM) }
    var body by rememberSaveable { mutableStateOf(initialEntry?.body ?: "") }
    var attachmentsInput by rememberSaveable {
        mutableStateOf(initialEntry?.attachments?.joinToString(", ") ?: "")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = if (existingId == null) {
                    stringResource(id = R.string.journal_editor_title_new)
                } else {
                    stringResource(id = R.string.journal_editor_title_edit)
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formatter.format(existingTimestamp.atZone(zoneId)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(id = R.string.journal_editor_mood_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            MoodSelector(
                selectedMood = mood,
                onMoodSelected = { mood = it }
            )
        }

        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            label = { Text(text = stringResource(id = R.string.journal_editor_body_label)) },
            placeholder = { Text(text = stringResource(id = R.string.journal_editor_body_placeholder)) },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Default
            ),
            singleLine = false,
            maxLines = 8
        )

        OutlinedTextField(
            value = attachmentsInput,
            onValueChange = { attachmentsInput = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.journal_editor_attachments_label)) },
            placeholder = { Text(text = stringResource(id = R.string.journal_editor_attachments_placeholder)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val attachments = attachmentsInput.split(',')
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                    val entry = JournalEntry(
                        id = existingId ?: UUID.randomUUID().toString(),
                        timestamp = existingTimestamp,
                        mood = mood,
                        body = body,
                        attachments = attachments
                    )
                    onSave(entry)
                },
                enabled = body.isNotBlank() && !isSaving
            ) {
                Text(text = stringResource(id = R.string.journal_editor_save))
            }

            TextButton(onClick = onCancel, enabled = !isSaving) {
                Text(text = stringResource(id = R.string.journal_editor_cancel))
            }

            if (existingId != null && onDelete != null) {
                TextButton(onClick = onDelete, enabled = !isSaving) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(text = stringResource(id = R.string.journal_editor_delete))
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun MoodSelector(
    selectedMood: JournalMood,
    onMoodSelected: (JournalMood) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JournalMood.values().forEach { mood ->
            FilterChip(
                selected = mood == selectedMood,
                onClick = { onMoodSelected(mood) },
                label = { Text(text = moodDisplayName(mood)) },
            )
        }
    }
}
