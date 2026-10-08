package io.github.seraphina.myledger.notification.parser

import io.github.seraphina.myledger.common.config.CommonConfig
import io.github.seraphina.myledger.common.model.ParsedPayment
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.common.model.RecordSource
import io.github.seraphina.myledger.utility.AmountUtility

abstract class BasePaymentNotificationParser : NotificationParser {
    protected abstract val source: RecordSource
    protected abstract val paymentKeywords: List<String>
    protected abstract val incomeTitle: String
    protected abstract val expenseTitle: String

    override fun supports(packageName: String): Boolean = packageName == source.packageName

    override fun parse(title: String, content: String): ParsedPayment? {
        val text = "$title $content"
        if (paymentKeywords.none { text.contains(it) }) return null
        val amountCents = AmountUtility.parseCents(text) ?: return null
        if (amountCents <= 0L) return null
        val direction = when {
            CommonConfig.INCOME_KEYWORDS.any { text.contains(it) } -> RecordDirection.INCOME
            CommonConfig.EXPENSE_KEYWORDS.any { text.contains(it) } -> RecordDirection.EXPENSE
            else -> return null
        }
        return ParsedPayment(
            direction = direction,
            amountCents = amountCents,
            title = if (direction == RecordDirection.INCOME) incomeTitle else expenseTitle,
            source = source
        )
    }
}
