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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.seraphina.myledger.R
import io.github.seraphina.myledger.common.model.LedgerRecord
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
    onSearchQueryChange: (String) -> Unit,
    onLoadMoreRecords: () -> Unit,
    onUpdateRecordNote: (Long, String) -> Unit,
    onDeleteRecord: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRecord by remember { mutableStateOf<LedgerRecord?>(null) }
    val listState = rememberLazyListState()
    val groupedRecords = remember(uiState.records) {
        uiState.records.groupBy { record -> TimeUtility.dayStart(record.timestamp) }
    }
    LaunchedEffect(uiState.searchQuery) {
        if (uiState.records.isNotEmpty()) listState.scrollToItem(0)
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
        SearchField(query = uiState.searchQuery, onQueryChange = onSearchQueryChange)
        if (uiState.searchQuery.isNotBlank()) {
            Text(
                text = stringResource(R.string.search_results_count, uiState.matchedRecordCount),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 4.dp)
            )
        }
        if (uiState.records.isEmpty()) {
            EmptyRecords(
                message = stringResource(
                    if (uiState.searchQuery.isBlank()) R.string.records_empty else R.string.search_empty
                ),
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                groupedRecords.forEach { (dayStart, dayRecords) ->
                    item(key = "day-$dayStart") {
                        DayHeader(
                            dayStart = dayStart,
                            expenseCents = uiState.dayExpenseTotals[dayStart] ?: 0L
                        )
                    }
                    items(items = dayRecords, key = { record -> record.id }) { record ->
                        RecordItem(record = record, onClick = { selectedRecord = record })
                    }
                }
                if (uiState.hasMoreRecords) {
                    item(key = "load-more-${uiState.records.size}") {
                        LaunchedEffect(Unit) { onLoadMoreRecords() }
                        Text(
                            text = stringResource(
                                R.string.search_load_more,
                                uiState.matchedRecordCount - uiState.records.size
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
    selectedRecord?.let { record ->
        RecordDetailDialog(
            record = record,
            onDismiss = { selectedRecord = null },
            onSaveNote = { note ->
                onUpdateRecordNote(record.id, note)
                selectedRecord = null
            },
            onDelete = {
                onDeleteRecord(record.id)
                selectedRecord = null
            }
        )
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = if (query.isEmpty()) {
            null
        } else {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.search_clear))
                }
            }
        },
        singleLine = true
    )
}

@Composable
private fun DayHeader(dayStart: Long, expenseCents: Long) {
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
private fun EmptyRecords(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
