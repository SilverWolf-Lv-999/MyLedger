package io.github.seraphina.myledger.notification

import io.github.seraphina.myledger.common.config.CommonConfig
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.common.model.RecordSource
import io.github.seraphina.myledger.notification.parser.PaymentParserRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PaymentParserRegistryTest {
    @Test
    fun parsesWechatPaymentNotification() {
        val payment = PaymentParserRegistry.parse(
            CommonConfig.WECHAT_PACKAGE,
            "微信支付",
            "微信支付 已支付 ¥25.00"
        )
        assertNotNull(payment)
        assertEquals(2500L, payment!!.amountCents)
        assertEquals(RecordDirection.EXPENSE, payment.direction)
        assertEquals(RecordSource.WECHAT, payment.source)
        assertEquals("微信支付", payment.title)
    }

    @Test
    fun parsesWechatIncomeNotification() {
        val payment = PaymentParserRegistry.parse(
            CommonConfig.WECHAT_PACKAGE,
            "微信收款助手",
            "微信支付收款 0.01 元"
        )
        assertNotNull(payment)
        assertEquals(1L, payment!!.amountCents)
        assertEquals(RecordDirection.INCOME, payment.direction)
        assertEquals("微信收款", payment.title)
    }

    @Test
    fun parsesWechatRefundNotification() {
        val payment = PaymentParserRegistry.parse(
            CommonConfig.WECHAT_PACKAGE,
            "微信支付",
            "微信支付 退款到账 12.34元"
        )
        assertNotNull(payment)
        assertEquals(1234L, payment!!.amountCents)
        assertEquals(RecordDirection.INCOME, payment.direction)
    }

    @Test
    fun parsesAlipayPaymentNotification() {
        val payment = PaymentParserRegistry.parse(
            CommonConfig.ALIPAY_PACKAGE,
            "支付宝",
            "你已成功付款 25.00 元"
        )
        assertNotNull(payment)
        assertEquals(2500L, payment!!.amountCents)
        assertEquals(RecordDirection.EXPENSE, payment.direction)
        assertEquals(RecordSource.ALIPAY, payment.source)
    }

    @Test
    fun parsesAlipayIncomeNotification() {
        val payment = PaymentParserRegistry.parse(
            CommonConfig.ALIPAY_PACKAGE,
            "支付宝",
            "你已成功收款 100 元"
        )
        assertNotNull(payment)
        assertEquals(10000L, payment!!.amountCents)
        assertEquals(RecordDirection.INCOME, payment.direction)
    }

    @Test
    fun ignoresChatNotificationWithAmount() {
        assertNull(
            PaymentParserRegistry.parse(
                CommonConfig.WECHAT_PACKAGE,
                "张三",
                "转账给你 50 元"
            )
        )
    }

    @Test
    fun ignoresPaymentTextFromUnknownPackage() {
        assertNull(
            PaymentParserRegistry.parse(
                "com.example.other",
                "微信支付",
                "微信支付 已支付 ¥25.00"
            )
        )
    }

    @Test
    fun ignoresNotificationWithoutAmount() {
        assertNull(
            PaymentParserRegistry.parse(
                CommonConfig.ALIPAY_PACKAGE,
                "支付宝",
                "你有新的消息"
            )
        )
    }
}
