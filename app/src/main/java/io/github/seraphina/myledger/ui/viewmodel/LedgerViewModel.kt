package io.github.seraphina.myledger.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.data.LedgerDatabase
import io.github.seraphina.myledger.data.repository.LedgerRepository
import io.github.seraphina.myledger.service.LedgerKeepAliveService
import io.github.seraphina.myledger.ui.state.LedgerUiState
import io.github.seraphina.myledger.utility.NotificationAccessUtility
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

    val uiState: StateFlow<LedgerUiState> = combine(
        repository.observeRecords(),
        repository.observeSettings(),
        notificationAccessGranted,
        batteryOptimizationIgnored
    ) { records, settings, accessGranted, batteryIgnored ->
        val incomeCents = records.filter { it.direction == RecordDirection.INCOME }.sumOf { it.amountCents }
        val expenseCents = records.filter { it.direction == RecordDirection.EXPENSE }.sumOf { it.amountCents }
        val initialAmountCents = settings.initialAmountCents
        LedgerUiState(
            isLoading = false,
            hasInitialAmount = initialAmountCents != null,
            initialAmountCents = initialAmountCents ?: 0L,
            balanceCents = (initialAmountCents ?: 0L) + incomeCents - expenseCents,
            incomeCents = incomeCents,
            expenseCents = expenseCents,
            records = records,
            autoRecordEnabled = settings.autoRecordEnabled,
            keepAliveEnabled = settings.keepAliveEnabled,
            notificationAccessGranted = accessGranted,
            batteryOptimizationIgnored = batteryIgnored
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

    fun setAutoRecordEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setAutoRecordEnabled(enabled) }
    }

    fun setKeepAliveEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setKeepAliveEnabled(enabled)
            LedgerKeepAliveService.sync(getApplication(), enabled)
        }
    }

    fun addManualRecord(direction: RecordDirection, amountCents: Long, note: String) {
        viewModelScope.launch { repository.addManualRecord(direction, amountCents, note) }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch { repository.deleteRecord(id) }
    }

    fun clearRecords() {
        viewModelScope.launch { repository.clearRecords() }
    }
}
