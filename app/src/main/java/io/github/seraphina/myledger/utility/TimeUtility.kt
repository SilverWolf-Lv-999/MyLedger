package io.github.seraphina.myledger.utility

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object TimeUtility {
    private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.CHINA)
    private val SHORT_FORMATTER = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss", Locale.CHINA)
    private val DAY_FORMATTER = DateTimeFormatter.ofPattern("M月d日 EEEE", Locale.CHINA)
    private val FULL_DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE", Locale.CHINA)
    private val FULL_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日 HH:mm:ss", Locale.CHINA)
    private val INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    private val INPUT_PARSE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s", Locale.CHINA)

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

    fun formatDayFull(timestamp: Long): String = toDate(timestamp).format(FULL_DAY_FORMATTER)

    fun formatTime(timestamp: Long): String = toDateTime(timestamp).format(TIME_FORMATTER)

    fun formatShort(timestamp: Long): String = toDateTime(timestamp).format(SHORT_FORMATTER)

    fun formatFull(timestamp: Long): String = toDateTime(timestamp).format(FULL_FORMATTER)

    fun formatInput(timestamp: Long): String = toDateTime(timestamp).format(INPUT_FORMATTER)

    fun parseInput(text: String): Long? = runCatching {
        LocalDateTime.parse(text.trim(), INPUT_PARSE_FORMATTER)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }.getOrNull()

    private fun toDate(timestamp: Long): LocalDate =
        Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

    private fun toDateTime(timestamp: Long): LocalDateTime =
        Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
}
