package me.zavdav.zcore.data

import com.github.cliftonlabs.json_simple.JsonObject
import me.zavdav.zcore.util.getSafeHeight
import me.zavdav.zcore.util.roundYaw
import org.bukkit.Bukkit
import org.bukkit.Location

object Warps : JsonData("warps.json") {

    var warps: MutableMap<String, Location> = mutableMapOf()

    init { deserialize() }

    override fun deserialize() {
        warps = json.map {
            val spawn = it.value as JsonObject
            it.key to
            Location(
                Bukkit.getWorld(spawn["world"].toString()),
                spawn["x"].toString().toDouble(),
                spawn["y"].toString().toDouble(),
                spawn["z"].toString().toDouble(),
                spawn["yaw"].toString().toFloat(),
                spawn["pitch"].toString().toFloat()
            )
        }.toMap().toMutableMap()
    }

    override fun serialize() {
        json = JsonObject(warps.map {
            it.key to
            JsonObject(mapOf(
                "world" to it.value.world.name,
                "x" to it.value.x,
                "y" to it.value.y,
                "z" to it.value.z,
                "pitch" to it.value.pitch,
                "yaw" to it.value.yaw
            ))
        }.toMap())
    }

    fun setWarp(name: String, location: Location) {
        location.x = location.blockX + 0.5
        location.y = location.blockY.toDouble()
        location.z = location.blockZ + 0.5
        location.pitch = 0f
        location.yaw = roundYaw(location.yaw).toFloat()

        warps[name] = location
    }

    fun removeWarp(name: String) {
        warps.entries.removeIf { it.key.equals(name, true) }
    }

    fun warpExists(name: String): Boolean = getWarp(name) != null

    fun getWarpLocation(name: String): Location? {
        val location = getWarp(name) ?: return null
        location.y = getSafeHeight(location).toDouble()
        return location
    }

    fun getWarp(name: String): Location? =
        warps.entries.firstOrNull { it.key.equals(name, true) }?.value

    fun getWarpName(name: String): String =
        warps.keys.firstOrNull { it.equals(name, true) } ?: name

    fun getWarps(): List<String> = warps.map { it.key }
}