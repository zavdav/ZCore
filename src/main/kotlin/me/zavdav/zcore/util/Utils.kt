package me.zavdav.zcore.util

import com.projectposeidon.api.PoseidonUUID
import com.projectposeidon.api.UUIDType
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.UUIDCache
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Utils.safeSubstring
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.inventory.ItemStack
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.hasPermission
import java.util.ResourceBundle
import java.util.UUID
import java.util.regex.Pattern
import kotlin.math.*

fun CommandSender.send(message: String, vararg pairs: Pair<String, Any>) =
    sendMessage(format(message, *pairs))

fun CommandSender.send(message: String, player: Player, vararg pairs: Pair<String, Any>) =
    sendMessage(format(message, player, *pairs))

fun CommandSender.sendTl(key: String, vararg pairs: Pair<String, Any>) =
    sendMessage(tl(key, *pairs))

fun CommandSender.sendTl(key: String, player: Player, vararg pairs: Pair<String, Any>) =
    sendMessage(tl(key, player, *pairs))

fun broadcast(message: String, vararg pairs: Pair<String, Any>) =
    Bukkit.broadcastMessage(format(message, *pairs))

fun broadcast(message: String, player: Player, vararg pairs: Pair<String, Any>) =
    Bukkit.broadcastMessage(format(message, player, *pairs))

fun broadcastTl(key: String, vararg pairs: Pair<String, Any>) =
    Bukkit.broadcastMessage(tl(key, *pairs))

fun broadcastTl(key: String, player: Player, vararg pairs: Pair<String, Any>) =
    Bukkit.broadcastMessage(tl(key, player, *pairs))

fun getMessage(key: String): String = Utils.bundle.getString(key)

fun tl(key: String, vararg pairs: Pair<String, Any>): String =
    format(getMessage(key), *pairs)

fun tl(key: String, player: Player, vararg pairs: Pair<String, Any>): String =
    tl(key, *pairs, "name" to player.name, "displayname" to player.displayName)

fun tlError(key: String, vararg pairs: Pair<String, Any>): String =
    tl("errorMessage", "message" to tl(key, *pairs))

fun format(string: String, player: Player, vararg pairs: Pair<String, Any>): String =
    format(string, "name" to player.name, "displayname" to player.displayName, *pairs)

fun format(string: String, vararg pairs: Pair<String, Any>): String {
    var message = colorize(string)
    for (pair in pairs) {
        message = message.replace("{${pair.first.uppercase()}}", pair.second.toString())
    }
    return message
}

fun assert(condition: Boolean, key: String, vararg pairs: Pair<String, Any>) {
    if (!condition) throw CommandException(tlError(key, *pairs))
}

fun assert(condition: Boolean, exception: CommandException) {
    if (!condition) throw exception
}

fun Player.kick(key: String, vararg pairs: Pair<String, Any>) =
    kickPlayer(tl(key, *pairs).safeSubstring(0, 100))

object Utils {

    val bundle: ResourceBundle = ResourceBundle.getBundle("messages")

    val UUID_PATTERN: Pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    val IPV4_PATTERN: Pattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")

    private val AIR_MATERIALS: Set<Int> = setOf(
        0, 6, 27, 28, 32, 37, 38, 39, 40, 50, 55, 60, 63, 64, 65,
        66, 68, 70, 71, 72, 75, 76, 77, 78, 83, 93, 94, 96, 136)

    fun String.safeSubstring(startIndex: Int, endIndex: Int): String =
        if (length <= endIndex) this else substring(startIndex, endIndex)

    @JvmStatic fun getPlayerFromUsername(name: String): Player {
        if (name.isEmpty()) throw PlayerNotFoundException(name)
        return Bukkit.matchPlayer(name).firstOrNull() ?: throw PlayerNotFoundException(name)
    }

    @JvmStatic fun getPlayerFromUUID(uuid: UUID): Player? =
        Bukkit.getOnlinePlayers().firstOrNull { player -> player.uniqueId == uuid }

    @JvmStatic fun getPlayerFromString(string: String): Player =
        getPlayerFromUUID(getUUIDFromString(string)) ?: throw PlayerNotFoundException(string)

    @JvmStatic fun getPlayersFromIP(ip: String): Set<Player> =
        Bukkit.getOnlinePlayers().filter { player -> player.address.address.hostAddress == ip }.toSet()

    @JvmStatic fun getUUIDFromUsername(name: String): UUID {
        try {
            return getPlayerFromUsername(name).uniqueId
        } catch (_: PlayerNotFoundException) {}

        return UUIDCache.getUUIDFromUsername(name) ?:
        when (PoseidonUUID.getPlayerUUIDCacheStatus(name)) {
            UUIDType.ONLINE -> PoseidonUUID.getPlayerUUIDFromCache(name, true)
            UUIDType.OFFLINE -> PoseidonUUID.getPlayerUUIDFromCache(name, false)
            else -> throw PlayerNotFoundException(name)
        }
    }

    @JvmStatic fun getUUIDFromString(string: String): UUID =
        if (UUID_PATTERN.matcher(string).matches())
            UUID.fromString(string) else getUUIDFromUsername(string)

    fun Player.isSelf(other: Player) = uniqueId == other.uniqueId

    fun PlayerLoginEvent.kickBanned(key: String, vararg pairs: Pair<String, Any>) =
        disallow(PlayerLoginEvent.Result.KICK_BANNED, tl(key, *pairs).safeSubstring(0, 100))

    fun PlayerLoginEvent.kickBannedIp(key: String, vararg pairs: Pair<String, Any>) =
        disallow(PlayerLoginEvent.Result.KICK_BANNED_IP, tl(key, *pairs).safeSubstring(0, 100))

    @JvmStatic fun updateVanishedPlayers() {
        for (target in Bukkit.getOnlinePlayers()) {
            val user = User.from(target)
            when (user.isVanished) {
                true -> Bukkit.getOnlinePlayers()
                    .filter { player -> !hasPermission(player, "zcore.vanish.bypass") }
                    .forEach { player -> player.hidePlayer(target) }
                false -> Bukkit.getOnlinePlayers()
                    .forEach { player -> player.showPlayer(target) }
            }
        }
    }

    @JvmStatic fun notifySocialSpy(player: Player, commandLine: String) {
        for (target in Bukkit.getOnlinePlayers()) {
            if (User.from(target).socialSpy) {
                target.send(Config.socialSpy, player, "command" to commandLine)
            }
        }
    }

    @JvmStatic fun getSafeHeight(loc: Location): Int {
        val world = loc.world
        val x = loc.blockX
        var y = ceil(loc.y).toInt()
        val z = loc.blockZ

        while (isBlockAboveAir(world, x, y, z)) {
            y -= 1
            if (y < 1) break
        }
        while (isBlockUnsafe(world, x, y, z)) {
            y += 1
            if (y > 127) throw UnsafeDestinationException()
        }
        return y
    }

    @JvmStatic private fun isBlockAboveAir(world: World, x: Int, y: Int, z: Int): Boolean =
        world.getBlockAt(x, y - 1, z).typeId in AIR_MATERIALS

    @JvmStatic private fun isBlockUnsafe(world: World, x: Int, y: Int, z: Int): Boolean {
        val below = world.getBlockAt(x, y - 1, z).typeId

        if (below == Material.LAVA.id || below == Material.STATIONARY_LAVA.id || below == Material.FIRE.id
            || world.getBlockAt(x, y, z).typeId !in AIR_MATERIALS
            || world.getBlockAt(x, y + 1, z).typeId !in AIR_MATERIALS) {
            return true
        }
        return isBlockAboveAir(world, x, y, z)
    }

    @JvmStatic fun roundYaw(float: Float): Int {
        val yaw = if (float < 0) float + 360 else float
        var closest =  -1
        var lowestDiff = Float.MAX_VALUE

        for (i in 0..360 step 90) {
            val diff = abs(yaw - i)
            if (diff < lowestDiff) {
                closest = i
                lowestDiff = diff
            }
        }
        return if (closest == 360) 0 else closest
    }

    @JvmStatic fun Double.roundTo(digits: Int): Double {
        if (digits < 0) throw RuntimeException()
        return (this * 10.0.pow(digits)).roundToLong() / 10.0.pow(digits)
    }

    fun ItemStack.copy(): ItemStack = ItemStack(this.typeId, this.amount, this.durability)
}