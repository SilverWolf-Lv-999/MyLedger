package io.github.seraphina.myledger.utility

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class TimeUtilityTest {
    @Test
    fun formatsTimeAndFullTextToSeconds() {
        val timestamp = millisOf(2026, 10, 8, 9, 5, 7)
        assertEquals("09:05:07", TimeUtility.formatTime(timestamp))
        assertEquals("10-08 09:05:07", TimeUtility.formatShort(timestamp))
        assertEquals("2026年10月8日 09:05:07", TimeUtility.formatFull(timestamp))
    }

    @Test
    fun formatsInputTextPaddedToSeconds() {
        assertEquals("2026-10-08 09:05:07", TimeUtility.formatInput(millisOf(2026, 10, 8, 9, 5, 7)))
    }

    @Test
    fun parsesInputTextWithPaddedAndRelaxedDigits() {
        val padded = TimeUtility.parseInput("2026-10-08 09:05:07")
        val relaxed = TimeUtility.parseInput("2026-10-8 9:5:7")
        assertNotNull(padded)
        assertNotNull(relaxed)
        assertEquals(padded, relaxed)
        assertEquals("2026-10-08 09:05:07", TimeUtility.formatInput(relaxed!!))
    }

    @Test
    fun parseInputRejectsInvalidText() {
        assertNull(TimeUtility.parseInput("2026/10/08 09:05:07"))
        assertNull(TimeUtility.parseInput(""))
    }

    private fun millisOf(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Long =
        LocalDateTime.of(year, month, day, hour, minute, second)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
}
