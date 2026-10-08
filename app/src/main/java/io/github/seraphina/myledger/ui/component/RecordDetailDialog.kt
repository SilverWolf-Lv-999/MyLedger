package io.github.seraphina.myledger.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.seraphina.myledger.R
import io.github.seraphina.myledger.common.model.LedgerRecord
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.ui.theme.ledgerAmountColor
import io.github.seraphina.myledger.utility.AmountUtility
import io.github.seraphina.myledger.utility.TimeUtility

@Composable
fun RecordDetailDialog(
    record: LedgerRecord,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = record.direction == RecordDirection.INCOME
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_record_detail_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailRow(
                    label = stringResource(R.string.detail_amount),
                    value = (if (isIncome) "+" else "-") + AmountUtility.formatWithSymbol(record.amountCents),
                    valueColor = ledgerAmountColor(record.direction)
                )
                DetailRow(
                    label = stringResource(R.string.detail_type),
                    value = stringResource(if (isIncome) R.string.direction_income else R.string.direction_expense)
                )
                DetailRow(
                    label = stringResource(R.string.detail_source),
                    value = record.source.label
                )
                DetailRow(
                    label = stringResource(R.string.detail_time),
                    value = TimeUtility.formatFull(record.timestamp)
                )
                if (record.note.isNotBlank()) {
                    DetailRow(
                        label = stringResource(R.string.detail_note),
                        value = record.note
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_close))
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.4f)
        )
    }
}
