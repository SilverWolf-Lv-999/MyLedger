package io.github.seraphina.myledger

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.seraphina.myledger.common.config.CommonConfig
import io.github.seraphina.myledger.common.model.ParsedPayment
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.common.model.RecordSource
import io.github.seraphina.myledger.data.LedgerDatabase
import io.github.seraphina.myledger.data.repository.LedgerRepository
import io.github.seraphina.myledger.notification.PaymentNotificationRecorder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LedgerRepositoryInstrumentedTest {
    @Test
    fun storesInitialAmountAndRecords() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(context, LedgerDatabase::class.java).build()
        val repository = LedgerRepository(database)

        repository.setInitialAmount(10000L)
        repository.addManualRecord(RecordDirection.EXPENSE, 2500L, "午餐")
        repository.recordPayment(
            ParsedPayment(RecordDirection.INCOME, 500L, "微信收款", RecordSource.WECHAT),
            "signature-1",
            System.currentTimeMillis()
        )
        val duplicated = repository.recordPayment(
            ParsedPayment(RecordDirection.INCOME, 500L, "微信收款", RecordSource.WECHAT),
            "signature-1",
            System.currentTimeMillis()
        )

        val records = repository.observeRecords().first()
        val settings = repository.observeSettings().first()
        assertEquals(2, records.size)
        assertEquals(2500L, records.first { it.direction == RecordDirection.EXPENSE }.amountCents)
        assertEquals(10000L, settings.initialAmountCents)
        assertTrue(settings.autoRecordEnabled)
        assertFalse(duplicated)
        database.close()
    }

    @Test
    fun clearsRecordsAndKeepsInitialAmount() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(context, LedgerDatabase::class.java).build()
        val repository = LedgerRepository(database)

        repository.setInitialAmount(5000L)
        repository.addManualRecord(RecordDirection.INCOME, 100L, "红包")
        repository.clearRecords()

        assertEquals(0, repository.observeRecords().first().size)
        assertEquals(5000L, repository.observeSettings().first().initialAmountCents)
        database.close()
    }

    @Test
    fun recordsWechatNotificationOnceAndIgnoresOtherPackages() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(context, LedgerDatabase::class.java).build()
        val repository = LedgerRepository(database)
        val recorder = PaymentNotificationRecorder(repository)
        val timestamp = System.currentTimeMillis()

        val recorded = recorder.record(
            CommonConfig.WECHAT_PACKAGE,
            "微信支付",
            "微信支付 已支付 ¥25.00",
            timestamp
        )
        val duplicated = recorder.record(
            CommonConfig.WECHAT_PACKAGE,
            "微信支付",
            "微信支付 已支付 ¥25.00",
            timestamp + 1000L
        )
        val chatMessage = recorder.record(
            CommonConfig.WECHAT_PACKAGE,
            "张三",
            "转账给你 50 元",
            timestamp
        )
        val unknownPackage = recorder.record(
            "com.example.other",
            "微信支付",
            "微信支付 已支付 ¥25.00",
            timestamp
        )

        val records = repository.observeRecords().first()
        assertTrue(recorded)
        assertFalse(duplicated)
        assertFalse(chatMessage)
        assertFalse(unknownPackage)
        assertEquals(1, records.size)
        assertEquals(2500L, records[0].amountCents)
        assertEquals(RecordDirection.EXPENSE, records[0].direction)
        assertEquals("微信支付", records[0].title)
        database.close()
    }

    @Test
    fun persistsKeepAlivePreference() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val database = Room.inMemoryDatabaseBuilder(context, LedgerDatabase::class.java).build()
        val repository = LedgerRepository(database)

        assertTrue(repository.isKeepAliveEnabled())
        repository.setKeepAliveEnabled(false)
        assertFalse(repository.isKeepAliveEnabled())
        assertFalse(repository.observeSettings().first().keepAliveEnabled)
        database.close()
    }
}
