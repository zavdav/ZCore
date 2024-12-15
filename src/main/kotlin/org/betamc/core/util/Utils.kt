package org.betamc.core.util

import com.projectposeidon.api.PoseidonUUID
import com.projectposeidon.api.UUIDType
import org.betamc.core.player.PlayerMap
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.hasPermission
import java.text.MessageFormat
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

object Utils {

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

    private val AIR_MATERIALS: MutableSet<Int> = HashSet()

    init {
        AIR_MATERIALS.add(Material.AIR.id)
        AIR_MATERIALS.add(Material.SAPLING.id)
        AIR_MATERIALS.add(Material.POWERED_RAIL.id)
        AIR_MATERIALS.add(Material.DETECTOR_RAIL.id)
        AIR_MATERIALS.add(Material.DEAD_BUSH.id)
        AIR_MATERIALS.add(Material.RAILS.id)
        AIR_MATERIALS.add(Material.YELLOW_FLOWER.id)
        AIR_MATERIALS.add(Material.RED_ROSE.id)
        AIR_MATERIALS.add(Material.RED_MUSHROOM.id)
        AIR_MATERIALS.add(Material.BROWN_MUSHROOM.id)
        AIR_MATERIALS.add(Material.SEEDS.id)
        AIR_MATERIALS.add(Material.SIGN_POST.id)
        AIR_MATERIALS.add(Material.WALL_SIGN.id)
        AIR_MATERIALS.add(Material.LADDER.id)
        AIR_MATERIALS.add(Material.SUGAR_CANE_BLOCK.id)
        AIR_MATERIALS.add(Material.REDSTONE_WIRE.id)
        AIR_MATERIALS.add(Material.REDSTONE_TORCH_OFF.id)
        AIR_MATERIALS.add(Material.REDSTONE_TORCH_ON.id)
        AIR_MATERIALS.add(Material.TORCH.id)
        AIR_MATERIALS.add(Material.SOIL.id)
        AIR_MATERIALS.add(Material.DIODE_BLOCK_OFF.id)
        AIR_MATERIALS.add(Material.DIODE_BLOCK_ON.id)
        AIR_MATERIALS.add(Material.TRAP_DOOR.id)
        AIR_MATERIALS.add(Material.STONE_BUTTON.id)
        AIR_MATERIALS.add(Material.STONE_PLATE.id)
        AIR_MATERIALS.add(Material.WOOD_PLATE.id)
        AIR_MATERIALS.add(Material.IRON_DOOR_BLOCK.id)
        AIR_MATERIALS.add(Material.WOODEN_DOOR.id)
        AIR_MATERIALS.add(Material.SNOW.id)
    }

    @JvmStatic fun format(obj: Any, vararg objects: Any): String =
        MessageFormat(obj.toString()).format(objects)

    @JvmStatic fun formatColorize(obj: Any, vararg objects: Any): String =
        MessageFormat(colorize(obj.toString())).format(objects)

    fun String.safeSubstring(startIndex: Int, endIndex: Int): String =
        if (length <= endIndex) this else substring(startIndex, endIndex)

    @JvmStatic fun getPlayerFromUsername(name: String): Player? = Bukkit.matchPlayer(name).firstOrNull()

    @JvmStatic fun getPlayerFromUUID(uuid: UUID): Player? =
        Bukkit.getOnlinePlayers().firstOrNull { player -> player.uniqueId == uuid }

    @JvmStatic fun getPlayersFromIP(ip: String): Set<Player> =
        Bukkit.getOnlinePlayers().filter { player -> player.address.address.hostAddress == ip }.toSet()

    @JvmStatic fun getUUIDFromUsername(name: String): UUID? {
        val player = getPlayerFromUsername(name)
        if (player != null) return player.uniqueId

        return when (PoseidonUUID.getPlayerUUIDCacheStatus(name)) {
            UUIDType.ONLINE -> PoseidonUUID.getPlayerUUIDFromCache(name, true)
            UUIDType.OFFLINE -> PoseidonUUID.getPlayerUUIDFromCache(name, false)
            else -> null
        }
    }

    @JvmStatic fun updateVanishedPlayers() {
        for (target in Bukkit.getOnlinePlayers()) {
            val bmcPlayer = PlayerMap.getPlayer(target)
            when (bmcPlayer.vanished) {
                true -> Bukkit.getOnlinePlayers()
                    .filter { player -> !hasPermission(player, "bmc.vanish.bypass") }
                    .forEach { player -> player.hidePlayer(target) }
                false -> Bukkit.getOnlinePlayers()
                    .forEach { player -> player.showPlayer(target) }
            }
        }
    }

    @JvmStatic fun getSafeHeight(loc: Location): Int {
        val world = loc.world
        val x = floor(loc.x).toInt()
        var y = ceil(loc.y).toInt()
        val z = floor(loc.z).toInt()

        while (isBlockAboveAir(world, x, y, z)) {
            y -= 1
            if (y < 1) break
        }
        while (isBlockUnsafe(world, x, y, z)) {
            y += 1
            if (y > 127) throw Exception()
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
}