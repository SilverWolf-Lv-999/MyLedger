package io.github.seraphina.myledger.notification.parser

import io.github.seraphina.myledger.common.config.CommonConfig
import io.github.seraphina.myledger.common.model.RecordSource

object AlipayNotificationParser : BasePaymentNotificationParser() {
    override val source = RecordSource.ALIPAY
    override val paymentKeywords = CommonConfig.ALIPAY_PAYMENT_KEYWORDS
    override val incomeTitle = "支付宝收款"
    override val expenseTitle = "支付宝支付"
}
