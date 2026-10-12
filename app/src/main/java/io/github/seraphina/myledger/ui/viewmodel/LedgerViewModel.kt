package io.github.seraphina.myledger.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.seraphina.myledger.common.config.ClientConfig
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.data.LedgerDatabase
import io.github.seraphina.myledger.data.repository.LedgerRepository
import io.github.seraphina.myledger.service.LedgerKeepAliveService
import io.github.seraphina.myledger.ui.state.LedgerUiState
import io.github.seraphina.myledger.utility.NotificationAccessUtility
import io.github.seraphina.myledger.utility.RecordSearchUtility
import io.github.seraphina.myledger.utility.TimeUtility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LedgerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LedgerRepository(LedgerDatabase.get(application))
    private val notificationAccessGranted = MutableStateFlow(false)
    private val batteryOptimizationIgnored = MutableStateFlow(false)
    private val searchQuery = MutableStateFlow("")
    private val visibleCount = MutableStateFlow(ClientConfig.RECORD_PAGE_SIZE)

    val uiState: StateFlow<LedgerUiState> = combine(
        repository.observeRecords(),
        repository.observeSettings(),
        notificationAccessGranted,
        batteryOptimizationIgnored,
        combine(searchQuery, visibleCount) { query, count -> query to count }
    ) { records, settings, accessGranted, batteryIgnored, search ->
        val (query, pageLimit) = search
        val keyword = query.trim()
        val matchedRecords = if (keyword.isEmpty()) {
            records
        } else {
            records.filter { record -> RecordSearchUtility.matches(record, keyword) }
        }
        val visibleRecords = matchedRecords.take(pageLimit)
        val incomeCents = records.filter { it.direction == RecordDirection.INCOME }.sumOf { it.amountCents }
        val expenseCents = records.filter { it.direction == RecordDirection.EXPENSE }.sumOf { it.amountCents }
        val dayExpenseTotals = HashMap<Long, Long>()
        for (record in matchedRecords) {
            if (record.direction != RecordDirection.EXPENSE) continue
            val dayStart = TimeUtility.dayStart(record.timestamp)
            dayExpenseTotals[dayStart] = (dayExpenseTotals[dayStart] ?: 0L) + record.amountCents
        }
        val initialAmountCents = settings.initialAmountCents
        LedgerUiState(
            isLoading = false,
            hasInitialAmount = initialAmountCents != null,
            initialAmountCents = initialAmountCents ?: 0L,
            balanceCents = (initialAmountCents ?: 0L) + incomeCents - expenseCents,
            incomeCents = incomeCents,
            expenseCents = expenseCents,
            records = visibleRecords,
            autoRecordEnabled = settings.autoRecordEnabled,
            keepAliveEnabled = settings.keepAliveEnabled,
            notificationAccessGranted = accessGranted,
            batteryOptimizationIgnored = batteryIgnored,
            searchQuery = query,
            totalRecordCount = records.size,
            matchedRecordCount = matchedRecords.size,
            hasMoreRecords = visibleRecords.size < matchedRecords.size,
            dayExpenseTotals = dayExpenseTotals
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), LedgerUiState())

    init {
        refreshSystemState()
    }

    fun refreshSystemState() {
        val application = getApplication<Application>()
        val granted = NotificationAccessUtility.isGranted(application)
        notificationAccessGranted.value = granted
        batteryOptimizationIgnored.value = LedgerKeepAliveService.isBatteryOptimizationIgnored(application)
        viewModelScope.launch {
            val keepAliveEnabled = repository.isKeepAliveEnabled()
            if (granted) NotificationAccessUtility.requestRebind(application)
            LedgerKeepAliveService.sync(application, keepAliveEnabled)
        }
    }

    fun setInitialAmount(cents: Long) {
        viewModelScope.launch { repository.setInitialAmount(cents) }
    }

    fun setSearchQuery(query: String) {
        visibleCount.value = ClientConfig.RECORD_PAGE_SIZE
        searchQuery.value = query
    }

    fun loadMoreRecords() {
        visibleCount.value += ClientConfig.RECORD_PAGE_SIZE
    }

    fun setAutoRecordEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setAutoRecordEnabled(enabled) }
    }

    fun setKeepAliveEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setKeepAliveEnabled(enabled)
            LedgerKeepAliveService.sync(getApplication(), enabled)
        }
    }

    fun addManualRecord(direction: RecordDirection, amountCents: Long, note: String, timestamp: Long) {
        viewModelScope.launch { repository.addManualRecord(direction, amountCents, note, timestamp) }
    }

    fun updateRecordNote(id: Long, note: String) {
        viewModelScope.launch { repository.updateNote(id, note) }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch { repository.deleteRecord(id) }
    }

    fun clearRecords() {
        viewModelScope.launch { repository.clearRecords() }
    }
}
