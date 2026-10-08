package io.github.seraphina.myledger.notification.parser

import io.github.seraphina.myledger.common.model.ParsedPayment

object PaymentParserRegistry {
    private val parsers: List<NotificationParser> = listOf(
        WechatPayNotificationParser,
        AlipayNotificationParser
    )

    fun parse(packageName: String, title: String, content: String): ParsedPayment? {
        for (parser in parsers) {
            if (!parser.supports(packageName)) continue
            val payment = parser.parse(title, content)
            if (payment != null) return payment
        }
        return null
    }
}
