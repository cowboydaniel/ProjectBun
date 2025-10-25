package com.example.babydevelopmenttracker.ui.journal

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.babydevelopmenttracker.R

@Composable
fun JournalExportDialog(
    entryCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isExporting: Boolean,
) {
    AlertDialog(
        onDismissRequest = { if (!isExporting) onDismiss() },
        title = { Text(text = stringResource(id = R.string.journal_export_title)) },
        text = {
            if (isExporting) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = stringResource(
                        id = R.string.journal_export_description,
                        entryCount
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isExporting) {
                Text(text = stringResource(id = R.string.journal_export_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isExporting) {
                Text(text = stringResource(id = R.string.journal_export_cancel))
            }
        }
    )
}
