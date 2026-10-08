package io.github.seraphina.myledger.data.repository

import io.github.seraphina.myledger.common.config.ClientConfig
import io.github.seraphina.myledger.common.config.CommonConfig
import io.github.seraphina.myledger.common.model.LedgerRecord
import io.github.seraphina.myledger.common.model.LedgerSettings
import io.github.seraphina.myledger.common.model.ParsedPayment
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.common.model.RecordSource
import io.github.seraphina.myledger.data.LedgerDatabase
import io.github.seraphina.myledger.data.entity.LedgerRecordEntity
import io.github.seraphina.myledger.data.entity.LedgerSettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LedgerRepository(database: LedgerDatabase) {
    private val recordDao = database.recordDao()
    private val settingsDao = database.settingsDao()

    fun observeRecords(): Flow<List<LedgerRecord>> =
        recordDao.observeAll().map { records -> records.map { it.toRecord() } }

    fun observeSettings(): Flow<LedgerSettings> = settingsDao.observe().map { settings ->
        LedgerSettings(
            initialAmountCents = settings?.initialAmountCents,
            autoRecordEnabled = settings?.autoRecordEnabled ?: ClientConfig.DEFAULT_AUTO_RECORD_ENABLED,
            keepAliveEnabled = settings?.keepAliveEnabled ?: ClientConfig.DEFAULT_KEEP_ALIVE_ENABLED
        )
    }

    suspend fun setInitialAmount(cents: Long) {
        val settings = settingsDao.find()
        settingsDao.upsert(
            LedgerSettingsEntity(
                initialAmountCents = cents,
                autoRecordEnabled = settings?.autoRecordEnabled ?: ClientConfig.DEFAULT_AUTO_RECORD_ENABLED,
                keepAliveEnabled = settings?.keepAliveEnabled ?: ClientConfig.DEFAULT_KEEP_ALIVE_ENABLED
            )
        )
    }

    suspend fun setAutoRecordEnabled(enabled: Boolean) {
        val settings = settingsDao.find()
        settingsDao.upsert(
            LedgerSettingsEntity(
                initialAmountCents = settings?.initialAmountCents,
                autoRecordEnabled = enabled,
                keepAliveEnabled = settings?.keepAliveEnabled ?: ClientConfig.DEFAULT_KEEP_ALIVE_ENABLED
            )
        )
    }

    suspend fun setKeepAliveEnabled(enabled: Boolean) {
        val settings = settingsDao.find()
        settingsDao.upsert(
            LedgerSettingsEntity(
                initialAmountCents = settings?.initialAmountCents,
                autoRecordEnabled = settings?.autoRecordEnabled ?: ClientConfig.DEFAULT_AUTO_RECORD_ENABLED,
                keepAliveEnabled = enabled
            )
        )
    }

    suspend fun isAutoRecordEnabled(): Boolean =
        settingsDao.find()?.autoRecordEnabled ?: ClientConfig.DEFAULT_AUTO_RECORD_ENABLED

    suspend fun isKeepAliveEnabled(): Boolean =
        settingsDao.find()?.keepAliveEnabled ?: ClientConfig.DEFAULT_KEEP_ALIVE_ENABLED

    suspend fun addManualRecord(direction: RecordDirection, amountCents: Long, note: String) {
        recordDao.insert(
            LedgerRecordEntity(
                direction = direction,
                amountCents = amountCents,
                title = note.ifBlank { ClientConfig.MANUAL_RECORD_TITLE },
                source = RecordSource.MANUAL,
                note = note,
                signature = ClientConfig.MANUAL_SIGNATURE,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun recordPayment(payment: ParsedPayment, signature: String, timestamp: Long): Boolean {
        if (recordDao.hasRecentSignature(signature, timestamp - CommonConfig.DEDUP_WINDOW_MILLIS)) return false
        recordDao.insert(
            LedgerRecordEntity(
                direction = payment.direction,
                amountCents = payment.amountCents,
                title = payment.title,
                source = payment.source,
                note = "",
                signature = signature,
                timestamp = timestamp
            )
        )
        return true
    }

    suspend fun deleteRecord(id: Long) {
        recordDao.deleteById(id)
    }

    suspend fun clearRecords() {
        recordDao.deleteAll()
    }

    private fun LedgerRecordEntity.toRecord() = LedgerRecord(
        id = id,
        direction = direction,
        amountCents = amountCents,
        title = title,
        source = source,
        note = note,
        timestamp = timestamp
    )
}
