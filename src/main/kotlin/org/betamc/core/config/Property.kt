package org.betamc.core.config

enum class Property(var value: Any) {

    format_broadcastFormat("&f[&cBroadcast&f] &a%message%");

    val key: String = this.name.replace("_".toRegex(), ".")

    override fun toString(): String = value.toString()

    fun toInt(): Int = toString().toInt()

    fun toLong(): Long = toString().toLong()

    fun toDouble(): Double = toString().toDouble()

    fun toBoolean(): Boolean = toString().toBoolean()
}