package org.poseidonplugins.zcore.util

import com.projectposeidon.api.PoseidonUUID
import com.projectposeidon.api.UUIDType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.exceptions.PlayerNotFoundException
import org.poseidonplugins.zcore.exceptions.UnsafeDestinationException
import org.poseidonplugins.zcore.player.PlayerMap
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.regex.Pattern
import kotlin.math.*

fun getMessage(key: String): String = Utils.bundle.getString(key)

fun format(key: String, vararg pairs: Pair<String, Any>, color: Boolean = true): String =
    formatString(getMessage(key), *pairs, color = color)

fun formatError(key: String, vararg pairs: Pair<String, Any>): String =
    format("errorMessage", "message" to format(key, *pairs))

fun formatString(string: String, vararg pairs: Pair<String, Any>, color: Boolean = true): String {
    var message = if (color) colorize(string) else string
    for (pair in pairs) {
        message = message.replace("{${pair.first.uppercase()}}", pair.second.toString())
    }
    return message
}

object Utils {

    val bundle: ResourceBundle = ResourceBundle.getBundle("messages")

    private val nf: NumberFormat = NumberFormat.getNumberInstance(Locale.US)
    val UUID_PATTERN: Pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
    val IPV4_PATTERN: Pattern = Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")
    val TIME_PATTERN: Pattern = Pattern.compile(
        "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
                + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE)

    private val AIR_MATERIALS: Set<Int> = setOf(
        0, 6, 27, 28, 32, 37, 38, 39, 40, 50, 55, 60, 63, 64, 65,
        66, 68, 70, 71, 72, 75, 76, 77, 78, 83, 93, 94, 96, 136)

    fun String.safeSubstring(startIndex: Int, endIndex: Int): String =
        if (length <= endIndex) this else substring(startIndex, endIndex)

    fun String.toIntOrDefault(default: Int) = toIntOrNull() ?: default

    fun String.toLongOrDefault(default: Long) = toLongOrNull() ?: default

    fun String.toDoubleOrDefault(default: Double) = toDoubleOrNull() ?: default

    fun String.toBooleanOrDefault(default: Boolean) = toBooleanStrictOrNull() ?: default

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

        return when (PoseidonUUID.getPlayerUUIDCacheStatus(name)) {
            UUIDType.ONLINE -> PoseidonUUID.getPlayerUUIDFromCache(name, true)
            UUIDType.OFFLINE -> PoseidonUUID.getPlayerUUIDFromCache(name, false)
            else -> throw PlayerNotFoundException(name)
        }
    }

    @JvmStatic fun getUUIDFromString(string: String): UUID =
        if (UUID_PATTERN.matcher(string).matches())
            UUID.fromString(string) else getUUIDFromUsername(string)

    fun Player.isSelf(other: Player) = uniqueId == other.uniqueId

    @JvmStatic fun updateVanishedPlayers() {
        for (target in Bukkit.getOnlinePlayers()) {
            val zPlayer = PlayerMap.getPlayer(target)
            when (zPlayer.vanished) {
                true -> Bukkit.getOnlinePlayers()
                    .filter { player -> !hasPermission(player, "zcore.vanish.bypass") }
                    .forEach { player -> player.hidePlayer(target) }
                false -> Bukkit.getOnlinePlayers()
                    .forEach { player -> player.showPlayer(target) }
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
        AIR_MATERIALS.contains(world.getBlockTypeIdAt(x, y - 1, z))

    @JvmStatic private fun isBlockUnsafe(world: World, x: Int, y: Int, z: Int): Boolean {
        val below = world.getBlockTypeIdAt(x, y - 1, z)

        if (below == Material.LAVA.id || below == Material.STATIONARY_LAVA.id || below == Material.FIRE.id
            || !AIR_MATERIALS.contains(world.getBlockTypeIdAt(x, y, z))
            || !AIR_MATERIALS.contains(world.getBlockTypeIdAt(x, y + 1, z))) {
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

    @JvmStatic fun formatDateDiff(from: LocalDateTime, to: LocalDateTime): String {
        var mutFrom = from.truncatedTo(ChronoUnit.SECONDS)
        val finalTo = to.truncatedTo(ChronoUnit.SECONDS)
        val sb = StringBuilder()

        val years = ChronoUnit.YEARS.between(mutFrom, finalTo)
        mutFrom = mutFrom.plusYears(years)
        val months = ChronoUnit.MONTHS.between(mutFrom, finalTo)
        mutFrom = mutFrom.plusMonths(months)
        val weeks = ChronoUnit.WEEKS.between(mutFrom, finalTo)
        mutFrom = mutFrom.plusWeeks(weeks)
        val days = ChronoUnit.DAYS.between(mutFrom, finalTo)
        mutFrom = mutFrom.plusDays(days)
        val hours = ChronoUnit.HOURS.between(mutFrom, finalTo)
        mutFrom = mutFrom.plusHours(hours)
        val minutes = ChronoUnit.MINUTES.between(mutFrom, finalTo)
        mutFrom = mutFrom.plusMinutes(minutes)
        val seconds = ChronoUnit.SECONDS.between(mutFrom, finalTo)

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

    @JvmStatic fun parseDateDiff(time: String): LocalDateTime {
        val matcher = TIME_PATTERN.matcher(time)
        if (!matcher.matches()) throw Exception()

        val years = matcher.group(1)?.toLongOrNull() ?: 0
        val months = matcher.group(2)?.toLongOrNull() ?: 0
        val weeks = matcher.group(3)?.toLongOrNull() ?: 0
        val days = matcher.group(4)?.toLongOrNull() ?: 0
        val hours = matcher.group(5)?.toLongOrNull() ?: 0
        val minutes = matcher.group(6)?.toLongOrNull() ?: 0
        val seconds = matcher.group(7)?.toLongOrNull() ?: 0

        return LocalDateTime.now()
            .plusYears(years)
            .plusMonths(months)
            .plusWeeks(weeks)
            .plusDays(days)
            .plusHours(hours)
            .plusMinutes(minutes)
            .plusSeconds(seconds)
    }

    @JvmStatic fun formatBalance(amount: Double): String {
        val string = "${Config.getString("currency")}${nf.format(amount)}"
        return if (string.endsWith(".00")) string.substring(0, string.length - 3) else string
    }

    @JvmStatic fun Double.roundTo(digits: Int): Double {
        if (digits < 0) throw RuntimeException()
        return (this * 10.0.pow(digits)).roundToLong() / 10.0.pow(digits)
    }

}