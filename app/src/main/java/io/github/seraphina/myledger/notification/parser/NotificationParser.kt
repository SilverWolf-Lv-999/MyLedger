package io.github.seraphina.myledger.notification.parser

import io.github.seraphina.myledger.common.model.ParsedPayment

interface NotificationParser {
    fun supports(packageName: String): Boolean

    fun parse(title: String, content: String): ParsedPayment?
}
