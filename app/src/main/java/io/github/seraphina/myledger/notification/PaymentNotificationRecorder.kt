package io.github.seraphina.myledger.notification

import io.github.seraphina.myledger.data.repository.LedgerRepository
import io.github.seraphina.myledger.notification.parser.PaymentParserRegistry

class PaymentNotificationRecorder(private val repository: LedgerRepository) {
    suspend fun record(packageName: String, title: String, content: String, timestamp: Long): Boolean {
        val payment = PaymentParserRegistry.parse(packageName, title, content) ?: return false
        if (!repository.isAutoRecordEnabled()) return false
        val signature = "$packageName $title $content"
        return repository.recordPayment(payment, signature, timestamp)
    }
}
