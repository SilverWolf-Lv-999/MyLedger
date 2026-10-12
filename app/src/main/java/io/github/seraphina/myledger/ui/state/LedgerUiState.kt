package io.github.seraphina.myledger.ui.state

import io.github.seraphina.myledger.common.model.LedgerRecord

data class LedgerUiState(
    val isLoading: Boolean = true,
    val hasInitialAmount: Boolean = false,
    val initialAmountCents: Long = 0L,
    val balanceCents: Long = 0L,
    val incomeCents: Long = 0L,
    val expenseCents: Long = 0L,
    val records: List<LedgerRecord> = emptyList(),
    val autoRecordEnabled: Boolean = true,
    val keepAliveEnabled: Boolean = true,
    val notificationAccessGranted: Boolean = false,
    val batteryOptimizationIgnored: Boolean = false,
    val searchQuery: String = "",
    val totalRecordCount: Int = 0,
    val matchedRecordCount: Int = 0,
    val hasMoreRecords: Boolean = false,
    val dayExpenseTotals: Map<Long, Long> = emptyMap()
)
