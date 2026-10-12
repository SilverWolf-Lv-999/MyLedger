package io.github.seraphina.myledger.notification

import io.github.seraphina.myledger.common.model.NotificationEvent
import io.github.seraphina.myledger.common.model.NotificationEventStatus
import io.github.seraphina.myledger.common.model.RecordSource
import io.github.seraphina.myledger.data.repository.LedgerRepository
import io.github.seraphina.myledger.notification.parser.PaymentParserRegistry

class PaymentNotificationRecorder(private val repository: LedgerRepository) {
    suspend fun record(packageName: String, title: String, content: String, timestamp: Long): Boolean {
        val source = RecordSource.entries.firstOrNull { it.packageName == packageName } ?: return false
        val payment = PaymentParserRegistry.parse(packageName, title, content)
        if (payment == null) {
            NotificationDiagnostics.record(
                NotificationEvent(timestamp, source, title, content, NotificationEventStatus.UNRECOGNIZED)
            )
            return false
        }
        if (!repository.isAutoRecordEnabled()) {
            NotificationDiagnostics.record(
                NotificationEvent(timestamp, source, title, content, NotificationEventStatus.DISABLED)
            )
            return false
        }
        val signature = "$packageName $title $content"
        val recorded = repository.recordPayment(payment, signature, timestamp)
        NotificationDiagnostics.record(
            NotificationEvent(
                timestamp,
                source,
                title,
                content,
                if (recorded) NotificationEventStatus.RECORDED else NotificationEventStatus.DUPLICATE
            )
        )
        return recorded
    }
}
