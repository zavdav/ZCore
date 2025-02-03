@file:JvmName("Utils")

package org.poseidonplugins.zimport.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun unixToDateTime(unixMillis: Long): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(unixMillis), ZoneId.systemDefault())