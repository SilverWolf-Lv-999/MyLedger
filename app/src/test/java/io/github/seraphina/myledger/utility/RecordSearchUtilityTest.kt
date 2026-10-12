package io.github.seraphina.myledger.utility

import io.github.seraphina.myledger.common.model.LedgerRecord
import io.github.seraphina.myledger.common.model.RecordDirection
import io.github.seraphina.myledger.common.model.RecordSource
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

class RecordSearchUtilityTest {
    private val expense = LedgerRecord(
        id = 1L,
        direction = RecordDirection.EXPENSE,
        amountCents = 25050L,
        title = "微信支付",
        source = RecordSource.WECHAT,
        note = "Team-Dinner",
        timestamp = millisOf(2026, 10, 8, 20, 21, 55)
    )

    @Test
    fun matchesTitleAndNote() {
        assertTrue(RecordSearchUtility.matches(expense, "微信"))
        assertTrue(RecordSearchUtility.matches(expense, "dinner"))
    }

    @Test
    fun matchesAmountByText() {
        assertTrue(RecordSearchUtility.matches(expense, "250.50"))
        assertTrue(RecordSearchUtility.matches(expense, "250.5"))
        assertTrue(RecordSearchUtility.matches(expense, "¥250.50"))
        assertTrue(RecordSearchUtility.matches(expense, "250元"))
        assertFalse(RecordSearchUtility.matches(expense, "2500"))
    }

    @Test
    fun matchesFormattedGroupingAmount() {
        val salary = expense.copy(
            direction = RecordDirection.INCOME,
            amountCents = 123456L,
            source = RecordSource.ALIPAY
        )
        assertTrue(RecordSearchUtility.matches(salary, "1,234.56"))
        assertTrue(RecordSearchUtility.matches(salary, "1234.56"))
    }

    @Test
    fun matchesSourceAndDirection() {
        assertTrue(RecordSearchUtility.matches(expense, "wechat"))
        assertFalse(RecordSearchUtility.matches(expense, "手动"))
        assertTrue(RecordSearchUtility.matches(expense, "支出"))
        assertTrue(RecordSearchUtility.matches(expense, "expense"))
        assertFalse(RecordSearchUtility.matches(expense, "收入"))
        assertFalse(RecordSearchUtility.matches(expense, "支付宝"))
    }

    @Test
    fun matchesDateTimeVariants() {
        assertTrue(RecordSearchUtility.matches(expense, "2026-10-08"))
        assertTrue(RecordSearchUtility.matches(expense, "2026-10-08 20:21:55"))
        assertTrue(RecordSearchUtility.matches(expense, "20:21"))
        assertTrue(RecordSearchUtility.matches(expense, "10月8日"))
        assertTrue(RecordSearchUtility.matches(expense, "2026年10月8日"))
        val weekday = LocalDate.of(2026, 10, 8).dayOfWeek.getDisplayName(TextStyle.FULL, Locale.CHINA)
        assertTrue(RecordSearchUtility.matches(expense, weekday))
        assertFalse(RecordSearchUtility.matches(expense, "2026-09-08"))
    }

    @Test
    fun matchesTodayAndYesterday() {
        val today = expense.copy(id = 2L, timestamp = System.currentTimeMillis())
        val yesterday = expense.copy(id = 3L, timestamp = System.currentTimeMillis() - 86_400_000L)
        assertTrue(RecordSearchUtility.matches(today, "今天"))
        assertTrue(RecordSearchUtility.matches(yesterday, "昨天"))
        assertFalse(RecordSearchUtility.matches(today, "昨天"))
    }

    @Test
    fun blankQueryMatchesEverything() {
        assertTrue(RecordSearchUtility.matches(expense, ""))
        assertTrue(RecordSearchUtility.matches(expense, "   "))
    }

    @Test
    fun unrelatedQueryDoesNotMatch() {
        assertFalse(RecordSearchUtility.matches(expense, "超市购物"))
    }

    private fun millisOf(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Long =
        LocalDateTime.of(year, month, day, hour, minute, second)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
}
