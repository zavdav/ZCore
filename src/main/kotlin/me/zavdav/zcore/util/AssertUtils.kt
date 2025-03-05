@file:JvmName("AssertUtils")

package me.zavdav.zcore.util

fun assert(condition: Boolean, key: String, vararg args: Any) {
    if (!condition) throw CommandException(tl(key, *args))
}

fun assert(condition: Boolean, exception: CommandException) {
    if (!condition) throw exception
}