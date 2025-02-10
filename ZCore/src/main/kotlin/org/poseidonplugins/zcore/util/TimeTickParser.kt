package org.poseidonplugins.zcore.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

object TimeTickParser {

    private const val TICKS_AT_MIDNIGHT: Long = 18000
    private const val TICKS_PER_DAY: Long = 24000
    private const val TICKS_PER_HOUR: Long = 1000
    private const val TICKS_PER_MINUTE: Double = 1000.0 / 60.0

    private val FORMAT_24: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")
    private val FORMAT_12: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mma")

    private val NAME_TO_TICKS: Map<String, Long> = mapOf(
        "sunrise" to 23000,
        "day" to 0,
        "morning" to 1000,
        "midday" to 6000,
        "noon" to 6000,
        "afternoon" to 9000,
        "sunset" to 12000,
        "night" to 14000,
        "midnight" to 18000
    )

    fun parse(input: String): Long {
        val string = input.lowercase().replace("[^A-Za-z0-9:]".toRegex(), "")

        try {
            return parseTicks(string)
        } catch (_: Exception) {}

        try {
            return parse24(string)
        } catch (_: Exception) {}

        try {
            return parse12(string)
        } catch (_: Exception) {}

        try {
            return parseAlias(string)
        } catch (_: Exception) {}

        throw NumberFormatException()
    }

    fun parseTicks(input: String): Long {
        if (!input.matches("^[0-9]+ti?c?k?s?$".toRegex())) {
            throw NumberFormatException()
        }

        val string = input.replace("[^0-9]".toRegex(), "")
        return string.toLong() % TICKS_PER_DAY
    }

    fun parse24(input: String): Long {
        if (!input.matches("^(?:[01]?\\d|2[0-4]):[0-5]\\d$".toRegex())) {
            throw NumberFormatException()
        }

        var hours = 0
        var minutes = 0
        val string = input.lowercase().replace("[^0-9]".toRegex(), "")

        when (string.length) {
            4 -> {
                hours += string.substring(0, 2).toInt()
                minutes += string.substring(2, 4).toInt()
            }
            3 -> {
                hours += string.substring(0, 1).toInt()
                minutes += string.substring(1, 3).toInt()
            }
            else -> throw NumberFormatException()
        }

        return timeToTicks(hours, minutes)
    }

    fun parse12(input: String): Long {
        if (!input.matches("^(0?[1-9]|1[0-2])(:[0-5]\\d)?(am|pm)$".toRegex())) {
            throw NumberFormatException()
        }

        var hours = 0
        var minutes = 0
        val string = input.lowercase().replace("[^0-9]".toRegex(), "")

        when (string.length) {
            4 -> {
                hours += string.substring(0, 2).toInt()
                minutes += string.substring(2, 4).toInt()
            }
            3 -> {
                hours += string.substring(0, 1).toInt()
                minutes += string.substring(1, 3).toInt()
            }
            2 -> hours += string.substring(0, 2).toInt()
            1 -> hours += string.substring(0, 1).toInt()
            else -> throw NumberFormatException()
        }

        if (input.endsWith("pm") && hours != 12) hours += 12
        if (input.endsWith("am") && hours == 12) hours = 0
        return timeToTicks(hours, minutes)
    }

    fun parseAlias(input: String): Long =
        NAME_TO_TICKS[input.lowercase()] ?: throw NumberFormatException()

    fun timeToTicks(hours: Int, minutes: Int): Long {
        var ticks = TICKS_AT_MIDNIGHT
        ticks += hours * TICKS_PER_HOUR
        ticks += (minutes * TICKS_PER_MINUTE).toLong()
        ticks %= TICKS_PER_DAY

        return ticks
    }

    fun format24(ticks: Long): String = FORMAT_24.format(ticksToTime(ticks))

    fun format12(ticks: Long): String = FORMAT_12.format(ticksToTime(ticks))

    fun formatTicks(ticks: Long): String = "${ticks % TICKS_PER_DAY}ticks"

    fun ticksToTime(ticks: Long): LocalTime {
        val correctedTime = (ticks + 6000) % TICKS_PER_DAY
        var hours = correctedTime / 1000
        var minutes = ceil(correctedTime % 1000 / (1000.0 / 60.0)).toLong()
        if (minutes == 60L) {
            hours += 1
            minutes = 0
        }
        if (hours == 24L) hours = 0

        return LocalTime.of(hours.toInt(), minutes.toInt())
    }
}