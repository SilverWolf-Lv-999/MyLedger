package io.github.seraphina.myledger.common.model

data class NotificationEvent(
    val timestamp: Long,
    val source: RecordSource,
    val title: String,
    val content: String,
    val status: NotificationEventStatus
)
