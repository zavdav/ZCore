package org.betamc.core.config

enum class Property(val key: String, var value: Any) {

    AUTO_SAVE_TIME("auto-save-time", 300),
    BAN_DEFAULT_REASON("ban.default-reason", "The ban hammer has spoken!"),
    BAN_PERMANENT("ban.permanent-ban", "&cYou have been permanently banned, reason: {0}"),
    BAN_TEMPORARY("ban.temporary-ban", "&cYou have been banned until {0}, reason: {1}"),
    IPBAN_PERMANENT("ban.permanent-ipban", "&cYour IP has been permanently banned, reason: {0}"),
    IPBAN_TEMPORARY("ban.temporary-ipban", "&cYour IP has been banned until {0}, reason: {1}"),
    BROADCAST_FORMAT("format.broadcast-format", "&d[Broadcast] %message%"),
    MULTIPLE_HOMES("multiple-homes", 10),
    HOMES_PER_PAGE("homes-per-page", 50);

    override fun toString(): String = value.toString()

    fun toInt(): Int = toString().toInt()

    fun toLong(): Long = toString().toLong()

    fun toDouble(): Double = toString().toDouble()

    fun toBoolean(): Boolean = toString().toBoolean()
}