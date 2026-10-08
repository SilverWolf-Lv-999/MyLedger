package io.github.seraphina.myledger.notification.parser

import io.github.seraphina.myledger.common.config.CommonConfig
import io.github.seraphina.myledger.common.model.RecordSource

object WechatPayNotificationParser : BasePaymentNotificationParser() {
    override val source = RecordSource.WECHAT
    override val paymentKeywords = CommonConfig.WECHAT_PAYMENT_KEYWORDS
    override val incomeTitle = "微信收款"
    override val expenseTitle = "微信支付"
}
