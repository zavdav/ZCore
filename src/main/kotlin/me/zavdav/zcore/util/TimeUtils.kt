@file:JvmName("TimeUtils")

package me.zavdav.zcore.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")

val TIME_PATTERN: Pattern = Pattern.compile(
    "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" +
    "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" +
    "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" +
    "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" +
    "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" +
    "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" +
    "(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
    Pattern.CASE_INSENSITIVE)

fun formatEpoch(millis: Long): String {
    val dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of("UTC"))
    return formatter.format(dateTime)
}

fun formatDuration(millis: Long): String {
    var duration = millis / 1000

    val years = duration / 31_536_000
    duration -= years * 31_536_000
    val months = duration / 2_419_200
    duration -= months * 2_419_200
    val weeks = duration / 604800
    duration -= weeks * 604800
    val days = duration / 86400
    duration -= days * 86400
    val hours = duration / 3600
    duration -= hours * 3600
    val minutes = duration / 60
    duration -= minutes * 60
    val seconds = duration

    val sb = StringBuilder()
    val units = listOf(years, months, weeks, days, hours, minutes, seconds)
    val names = listOf(
        "year", "years",
        "month", "months",
        "week", "weeks",
        "day", "days",
        "hour", "hours",
        "minute", "minutes",
        "second", "seconds"
    )
    for (i in units.indices) {
        if (units[i] > 0) {
            sb.append("${units[i]} ${names[i * 2 + if (units[i] > 1) 1 else 0]} ")
        }
    }
    return if (sb.isEmpty()) "0 seconds" else sb.substring(0, sb.length - 1)
}

fun parseDuration(time: String): Long {
    val matcher = TIME_PATTERN.matcher(time)
    if (!matcher.matches()) throw RuntimeException()

    val years = matcher.group(1)?.toLongOrNull() ?: 0
    val months = matcher.group(2)?.toLongOrNull() ?: 0
    val weeks = matcher.group(3)?.toLongOrNull() ?: 0
    val days = matcher.group(4)?.toLongOrNull() ?: 0
    val hours = matcher.group(5)?.toLongOrNull() ?: 0
    val minutes = matcher.group(6)?.toLongOrNull() ?: 0
    val seconds = matcher.group(7)?.toLongOrNull() ?: 0

    return years * 31_536_000 +
            months * 2_419_200 +
            weeks * 604800 +
            days * 86400 +
            hours * 3600 +
            minutes * 60 +
            seconds
}