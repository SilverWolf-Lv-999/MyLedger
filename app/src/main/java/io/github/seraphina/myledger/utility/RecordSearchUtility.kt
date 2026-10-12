package io.github.seraphina.myledger.utility

import io.github.seraphina.myledger.common.model.LedgerRecord
import io.github.seraphina.myledger.common.model.RecordDirection
import java.util.Locale

object RecordSearchUtility {
    private val AMOUNT_PATTERN = Regex("""^\d+(?:\.\d{1,2})?$""")

    fun matches(record: LedgerRecord, query: String): Boolean {
        val keyword = query.trim()
        if (keyword.isEmpty()) return true
        val normalized = keyword.lowercase(Locale.ROOT)
        val amount = AmountUtility.format(record.amountCents)
        val haystack = buildString {
            append(record.title).append(' ')
            append(record.note).append(' ')
            append(record.source.label).append(' ')
            append(record.source.name).append(' ')
            append(
                if (record.direction == RecordDirection.INCOME) {
                    "收入 入账 INCOME"
                } else {
                    "支出 出账 EXPENSE"
                }
            ).append(' ')
            append(amount).append(' ')
            append(amount.replace(",", "")).append(' ')
            append(AmountUtility.formatWithSymbol(record.amountCents)).append(' ')
            append(TimeUtility.formatFull(record.timestamp)).append(' ')
            append(TimeUtility.formatInput(record.timestamp)).append(' ')
            append(TimeUtility.formatTime(record.timestamp)).append(' ')
            append(TimeUtility.formatDayLabel(record.timestamp)).append(' ')
            append(TimeUtility.formatDayFull(record.timestamp))
        }
        if (haystack.lowercase(Locale.ROOT).contains(normalized)) return true
        val amountKeyword = keyword
            .replace(",", "")
            .replace("¥", "")
            .replace("￥", "")
            .replace("元", "")
            .trim()
        return AMOUNT_PATTERN.matches(amountKeyword) &&
            amount.replace(",", "").contains(amountKeyword)
    }
}
