package io.github.seraphina.myledger.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.github.seraphina.myledger.MainActivity
import io.github.seraphina.myledger.R
import io.github.seraphina.myledger.common.config.CommonConfig
import io.github.seraphina.myledger.utility.NotificationAccessUtility

class LedgerKeepAliveService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val manager = getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CommonConfig.KEEP_ALIVE_CHANNEL_ID) == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CommonConfig.KEEP_ALIVE_CHANNEL_ID,
                    getString(R.string.keep_alive_channel_name),
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(CommonConfig.KEEP_ALIVE_NOTIFICATION_ID, buildNotification())
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (NotificationAccessUtility.isGranted(this)) scheduleRestart(this)
        super.onTaskRemoved(rootIntent)
    }

    private fun buildNotification(): Notification = NotificationCompat.Builder(
        this, CommonConfig.KEEP_ALIVE_CHANNEL_ID
    )
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(getString(R.string.keep_alive_title))
        .setContentText(getString(R.string.keep_alive_text))
        .setContentIntent(activityIntent(this))
        .setOngoing(true)
        .setShowWhen(false)
        .setSilent(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)
        .build()

    private fun activityIntent(context: Context): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    companion object {
        fun sync(context: Context, keepAliveEnabled: Boolean) {
            val application = context.applicationContext
            if (keepAliveEnabled && NotificationAccessUtility.isGranted(application)) {
                start(application)
            } else {
                stop(application)
            }
        }

        fun start(context: Context) {
            try {
                ContextCompat.startForegroundService(context, Intent(context, LedgerKeepAliveService::class.java))
            } catch (ignored: IllegalStateException) {
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, LedgerKeepAliveService::class.java))
        }

        fun scheduleRestart(context: Context) {
            val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
            val pendingIntent = PendingIntent.getForegroundService(
                context,
                1,
                Intent(context, LedgerKeepAliveService::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + CommonConfig.KEEP_ALIVE_RESTART_DELAY_MILLIS,
                pendingIntent
            )
        }

        fun isBatteryOptimizationIgnored(context: Context): Boolean =
            context.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(context.packageName)

        fun requestIgnoreBatteryOptimizations(context: Context) {
            openSystemPage(
                context,
                Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:${context.packageName}")
                )
            )
        }

        fun openAutoStartSettings(context: Context) {
            val candidates = listOf(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                ),
                ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"
                ),
                ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                ),
                ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            )
            for (component in candidates) {
                val intent = Intent().setComponent(component)
                if (context.packageManager.resolveActivity(intent, 0) != null) {
                    openSystemPage(context, intent)
                    return
                }
            }
            openSystemPage(
                context,
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))
            )
        }

        private fun openSystemPage(context: Context, intent: Intent) {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}
