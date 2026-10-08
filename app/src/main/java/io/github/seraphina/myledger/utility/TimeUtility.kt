package io.github.seraphina.myledger.utility

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtility {
    private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.CHINA)
    private val DAY_FORMATTER = DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.CHINA)
    private val FULL_DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE", Locale.CHINA)
    private val FULL_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm", Locale.CHINA)

    fun dayStart(timestamp: Long): Long =
        toDate(timestamp).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun formatDayLabel(timestamp: Long): String {
        val date = toDate(timestamp)
        val today = LocalDate.now()
        return when (date) {
            today -> "今天"
            today.minusDays(1) -> "昨天"
            else -> if (date.year == today.year) date.format(DAY_FORMATTER) else date.format(FULL_DAY_FORMATTER)
        }
    }

    fun formatTime(timestamp: Long): String = toDateTime(timestamp).format(TIME_FORMATTER)

    fun formatFull(timestamp: Long): String = toDateTime(timestamp).format(FULL_FORMATTER)

    private fun toDate(timestamp: Long): LocalDate =
        Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

    private fun toDateTime(timestamp: Long): LocalDateTime =
        Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
}
