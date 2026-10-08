package io.github.seraphina.myledger.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.seraphina.myledger.R
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.utility.AmountUtility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualRecordDialog(
    onDismiss: () -> Unit,
    onConfirm: (RecordDirection, Long, String) -> Unit
) {
    var direction by rememberSaveable { mutableStateOf(RecordDirection.EXPENSE) }
    var amountText by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    val amountCents = AmountUtility.parseInputCents(amountText)
    val isError = amountText.isNotBlank() && (amountCents == null || amountCents <= 0L)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_manual_record_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = direction == RecordDirection.EXPENSE,
                        onClick = { direction = RecordDirection.EXPENSE },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text(stringResource(R.string.direction_expense))
                    }
                    SegmentedButton(
                        selected = direction == RecordDirection.INCOME,
                        onClick = { direction = RecordDirection.INCOME },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text(stringResource(R.string.direction_income))
                    }
                }
                AmountField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    isError = isError
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { input -> note = input.take(40) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.field_note)) },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (amountCents != null && amountCents > 0L) onConfirm(direction, amountCents, note.trim())
                },
                enabled = amountCents != null && amountCents > 0L
            ) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
