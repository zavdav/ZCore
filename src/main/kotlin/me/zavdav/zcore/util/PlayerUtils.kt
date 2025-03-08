@file:JvmName("PlayerUtils")

package me.zavdav.zcore.util

import com.projectposeidon.api.PoseidonUUID
import com.projectposeidon.api.UUIDType
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.UUIDCache
import me.zavdav.zcore.hooks.permissions.PermissionHandler
import me.zavdav.zcore.user.User
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerLoginEvent
import java.util.UUID
import java.util.regex.Pattern

val UUID_PATTERN: Pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
val IPV4_PATTERN: Pattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")

fun getPlayerFromUsername(name: String): Player {
    if (name.isEmpty()) throw MiscellaneousException(tl("playerNotFound", name))
    return Bukkit.matchPlayer(name).firstOrNull() ?: throw MiscellaneousException(tl("playerNotFound", name))
}

fun getPlayerFromUUID(uuid: UUID): Player? =
    Bukkit.getOnlinePlayers().firstOrNull { it.uniqueId == uuid }

fun getPlayerFromString(string: String): Player =
    getPlayerFromUUID(getUUIDFromString(string)) ?: throw MiscellaneousException(tl("playerNotFound", string))

fun getPlayersFromIP(ip: String): Set<Player> =
    Bukkit.getOnlinePlayers().filter { it.address.address.hostAddress == ip }.toSet()

fun getUUIDFromUsername(name: String): UUID {
    runCatching {
        return getPlayerFromUsername(name).uniqueId
    }

    return UUIDCache.getUUIDFromUsername(name) ?:
    when (PoseidonUUID.getPlayerUUIDCacheStatus(name)) {
        UUIDType.ONLINE -> PoseidonUUID.getPlayerUUIDFromCache(name, true)
        UUIDType.OFFLINE -> PoseidonUUID.getPlayerUUIDFromCache(name, false)
        else -> throw MiscellaneousException(tl("playerNotFound", name))
    }
}

fun getUUIDFromString(string: String): UUID =
    if (UUID_PATTERN.matcher(string).matches())
        UUID.fromString(string) else getUUIDFromUsername(string)

fun getUsernameFromUUID(uuid: UUID): String? =
    UUIDCache.getUsernameFromUUID(uuid)

fun CommandSender.isAuthorized(permission: String): Boolean {
    return when (this) {
        is Player -> PermissionHandler.hasPermission(this, permission)
        is ConsoleCommandSender -> true
        else -> false
    }
}

inline fun CommandSender.assertOrSend(key: String, vararg args: Any,
                                      condition: (CommandSender) -> Boolean)
{
    if (!condition(this)) throw CommandException(this, tl(key, *args))
}

fun String.trimTo100(): String =
    if (length <= 100) this else substring(0, 100)

fun Player.kick(key: String, vararg args: Any) =
    kickPlayer(tl(key, *args).trimTo100())

fun PlayerLoginEvent.kickBanned(key: String, vararg args: Any) =
    disallow(PlayerLoginEvent.Result.KICK_BANNED, tl(key, *args).trimTo100())

fun PlayerLoginEvent.kickBannedIP(key: String, vararg args: Any) =
    disallow(PlayerLoginEvent.Result.KICK_BANNED_IP, tl(key, *args).trimTo100())

fun updateVanishedPlayers() {
    for (target in Bukkit.getOnlinePlayers()) {
        val user = User.from(target)
        when (user.isVanished) {
            true -> Bukkit.getOnlinePlayers()
                .filter { !it.isAuthorized("zcore.vanish.bypass") }
                .forEach { it.hidePlayer(target) }
            false -> Bukkit.getOnlinePlayers()
                .forEach { it.showPlayer(target) }
        }
    }
}

fun notifySocialSpy(player: Player, commandLine: String) {
    for (target in Bukkit.getOnlinePlayers()) {
        if (User.from(target).socialSpy) {
            target.send(Config.socialSpy,
                "name" to player.name, "displayname" to player.displayName, "command" to commandLine)
        }
    }
}