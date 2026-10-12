package io.github.seraphina.myledger.notification

import io.github.seraphina.myledger.common.model.NotificationEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NotificationDiagnostics {
    private const val MAX_EVENTS = 12

    private val eventFlow = MutableStateFlow<List<NotificationEvent>>(emptyList())
    private val connectedFlow = MutableStateFlow(false)

    val events: StateFlow<List<NotificationEvent>> = eventFlow.asStateFlow()

    val connected: StateFlow<Boolean> = connectedFlow.asStateFlow()

    fun record(event: NotificationEvent) {
        eventFlow.value = (listOf(event) + eventFlow.value).take(MAX_EVENTS)
    }

    fun setConnected(value: Boolean) {
        connectedFlow.value = value
    }
}
