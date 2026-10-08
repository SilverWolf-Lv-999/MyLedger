package io.github.seraphina.myledger.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.seraphina.myledger.R
import io.github.seraphina.myledger.common.model.LedgerRecord
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.ui.component.BalanceBand
import io.github.seraphina.myledger.ui.component.NotificationAccessBanner
import io.github.seraphina.myledger.ui.component.RecordDetailDialog
import io.github.seraphina.myledger.ui.component.RecordItem
import io.github.seraphina.myledger.ui.state.LedgerUiState
import io.github.seraphina.myledger.utility.AmountUtility
import io.github.seraphina.myledger.utility.TimeUtility

@Composable
fun LedgerScreen(
    uiState: LedgerUiState,
    onOpenNotificationSettings: () -> Unit,
    onDeleteRecord: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRecord by remember { mutableStateOf<LedgerRecord?>(null) }
    val groupedRecords = remember(uiState.records) {
        uiState.records.groupBy { record -> TimeUtility.dayStart(record.timestamp) }
    }
    Column(modifier = modifier.fillMaxSize()) {
        BalanceBand(
            initialAmountCents = uiState.initialAmountCents,
            balanceCents = uiState.balanceCents,
            incomeCents = uiState.incomeCents,
            expenseCents = uiState.expenseCents
        )
        if (!uiState.notificationAccessGranted) {
            NotificationAccessBanner(onOpenSettings = onOpenNotificationSettings)
        }
        if (uiState.records.isEmpty()) {
            EmptyRecords(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                groupedRecords.forEach { (dayStart, dayRecords) ->
                    item(key = "day-$dayStart") {
                        DayHeader(dayStart = dayStart, dayRecords = dayRecords)
                    }
                    items(items = dayRecords, key = { record -> record.id }) { record ->
                        RecordItem(record = record, onClick = { selectedRecord = record })
                    }
                }
            }
        }
    }
    selectedRecord?.let { record ->
        RecordDetailDialog(
            record = record,
            onDismiss = { selectedRecord = null },
            onDelete = {
                onDeleteRecord(record.id)
                selectedRecord = null
            }
        )
    }
}

@Composable
private fun DayHeader(dayStart: Long, dayRecords: List<LedgerRecord>) {
    val expenseCents = dayRecords
        .filter { record -> record.direction == RecordDirection.EXPENSE }
        .sumOf { record -> record.amountCents }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = TimeUtility.formatDayLabel(dayStart),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (expenseCents > 0L) {
            Text(
                text = "-" + AmountUtility.formatWithSymbol(expenseCents),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyRecords(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.records_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
