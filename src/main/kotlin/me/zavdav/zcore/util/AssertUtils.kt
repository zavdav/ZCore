@file:JvmName("AssertUtils")

package me.zavdav.zcore.util

fun assert(condition: Boolean, key: String, vararg pairs: Pair<String, Any>) {
    if (!condition) throw CommandException(tlError(key, *pairs))
}

fun assert(condition: Boolean, exception: CommandException) {
    if (!condition) throw exception
}