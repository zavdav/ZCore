package org.poseidonplugins.zcore.config

import org.poseidonplugins.zcore.ZCore

enum class Property(val key: String, val default: Any) {

    AUTO_SAVE_TIME("auto-save-time", 300),
    BAN_DEFAULT_REASON("ban.default-reason", "The ban hammer has spoken!"),
    BAN_PERMANENT("ban.permanent-ban", "&cYou have been permanently banned: {REASON}"),
    BAN_TEMPORARY("ban.temporary-ban", "&cYou have been banned until {DATETIME}: {REASON}"),
    IPBAN_PERMANENT("ban.permanent-ipban", "&cYour IP has been permanently banned: {REASON}"),
    IPBAN_TEMPORARY("ban.temporary-ipban", "&cYour IP has been banned until {DATETIME}: {REASON}"),
    KICK_DEFAULT_REASON("kick.default-reason", "Kicked from server"),
    KICK_FORMAT("kick.format", "&cYou have been kicked: {REASON}"),
    BROADCAST_FORMAT("format.broadcast-format", "&d[Broadcast] {MESSAGE}"),
    MAX_BALANCE("max-balance", 10000000000000),
    MULTIPLE_HOMES("multiple-homes", 10),
    MOTD("motd", mutableListOf(
        "&eWelcome, {USERNAME}&e!",
        "&bType /help for a list of commands.",
        "&7Online players: &f{LIST}")),
    HOMES_PER_PAGE("homes-per-page", 50),
    RULES("rules", mutableListOf(
        "&c1. Be respectful",
        "&c2. No griefing",
        "&c3. No cheating"
    ));

    override fun toString(): String =
        (ZCore.config.getProperty(key) ?: default).toString()

    fun toInt(): Int = toString().toIntOrNull() ?: default.toString().toInt()

    fun toUInt(): Int = toInt().coerceAtLeast(0)

    fun toLong(): Long = toString().toLongOrNull() ?: default.toString().toLong()

    fun toULong(): Long = toLong().coerceAtLeast(0)

    fun toDouble(): Double = toString().toDouble()

    fun toBoolean(): Boolean = toString().toBoolean()

    fun toList(): MutableList<String> {
        return try {
            (ZCore.config.getProperty(key) as List<String>).toMutableList()
        } catch (e: Exception) {
            default as MutableList<String>
        }
    }
}