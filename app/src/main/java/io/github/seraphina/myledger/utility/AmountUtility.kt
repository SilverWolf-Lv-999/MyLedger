package io.github.seraphina.myledger.utility

import io.github.seraphina.myledger.common.config.ClientConfig
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

object AmountUtility {
    private val YUAN_PATTERN = Regex("""(\d[\d,]*(?:\.\d{1,2})?)\s*元""")
    private val SYMBOL_PATTERN = Regex("""[¥￥]\s*(\d[\d,]*(?:\.\d{1,2})?)""")
    private val DECIMAL_PATTERN = Regex("""\d[\d,]*\.\d{1,2}""")

    fun parseCents(text: String?): Long? {
        if (text.isNullOrBlank()) return null
        val raw = YUAN_PATTERN.find(text)?.groupValues?.get(1)
            ?: SYMBOL_PATTERN.find(text)?.groupValues?.get(1)
            ?: DECIMAL_PATTERN.find(text)?.value
            ?: return null
        return toCents(raw)
    }

    fun parseInputCents(text: String?): Long? {
        if (text.isNullOrBlank()) return null
        val cleaned = text.replace(",", "").replace(ClientConfig.CURRENCY_SYMBOL, "").replace("￥", "").trim()
        return toCents(cleaned)
    }

    fun format(cents: Long): String =
        String.format(Locale.CHINA, "%,.2f", BigDecimal.valueOf(cents).movePointLeft(2))

    fun formatWithSymbol(cents: Long): String = ClientConfig.CURRENCY_SYMBOL + format(cents)

    private fun toCents(raw: String): Long? = raw.replace(",", "")
        .toBigDecimalOrNull()
        ?.movePointRight(2)
        ?.setScale(0, RoundingMode.HALF_UP)
        ?.toLong()
}
