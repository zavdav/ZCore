package org.betamc.core.config

enum class Property(val key: String, var value: Any) {

    BROADCAST_FORMAT("format.broadcast-format", "&f[&cBroadcast&f] &a%message%");

    override fun toString(): String = value.toString()

    fun toInt(): Int = toString().toInt()

    fun toLong(): Long = toString().toLong()

    fun toDouble(): Double = toString().toDouble()

    fun toBoolean(): Boolean = toString().toBoolean()
}