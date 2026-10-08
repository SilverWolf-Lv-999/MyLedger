package io.github.seraphina.myledger.notification

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import io.github.seraphina.myledger.data.LedgerDatabase
import io.github.seraphina.myledger.data.repository.LedgerRepository
import io.github.seraphina.myledger.service.LedgerKeepAliveService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class PaymentNotificationListener : NotificationListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val recorder by lazy { PaymentNotificationRecorder(LedgerRepository(LedgerDatabase.get(this))) }

    override fun onListenerConnected() {
        super.onListenerConnected()
        LedgerKeepAliveService.start(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        if (sbn.isOngoing) return
        if (notification.flags and Notification.FLAG_GROUP_SUMMARY != 0) return
        val extras = notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val content = collectContent(extras)
        if (title.isBlank() && content.isBlank()) return
        val timestamp = if (sbn.postTime > 0L) sbn.postTime else System.currentTimeMillis()
        scope.launch {
            recorder.record(sbn.packageName, title, content, timestamp)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun collectContent(extras: Bundle): String {
        val parts = ArrayList<CharSequence>(8)
        val keys = arrayOf(
            Notification.EXTRA_TEXT,
            Notification.EXTRA_BIG_TEXT,
            Notification.EXTRA_SUB_TEXT,
            Notification.EXTRA_SUMMARY_TEXT
        )
        for (key in keys) {
            extras.getCharSequence(key)?.let(parts::add)
        }
        extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.let(parts::addAll)
        return parts.joinToString(" ")
    }
}
