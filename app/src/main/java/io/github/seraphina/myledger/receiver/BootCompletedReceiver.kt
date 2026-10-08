package io.github.seraphina.myledger.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.seraphina.myledger.service.LedgerKeepAliveService

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        LedgerKeepAliveService.start(context)
    }
}
