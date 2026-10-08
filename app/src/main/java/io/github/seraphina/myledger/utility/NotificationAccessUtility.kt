package io.github.seraphina.myledger.utility

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService
import io.github.seraphina.myledger.notification.PaymentNotificationListener

object NotificationAccessUtility {
    fun isGranted(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver, "enabled_notification_listeners"
        ) ?: return false
        val target = ComponentName(context, PaymentNotificationListener::class.java)
        return enabledListeners.split(':').any { ComponentName.unflattenFromString(it) == target }
    }

    fun openSettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun requestRebind(context: Context) {
        NotificationListenerService.requestRebind(ComponentName(context, PaymentNotificationListener::class.java))
    }
}
