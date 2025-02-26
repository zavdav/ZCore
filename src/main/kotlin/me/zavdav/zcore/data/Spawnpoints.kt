package me.zavdav.zcore.data

import com.github.cliftonlabs.json_simple.JsonObject
import me.zavdav.zcore.util.roundYaw
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

object Spawnpoints : JsonData("spawnpoints.json") {

    var spawnPoints: MutableMap<String, Location> = mutableMapOf()

    init { deserialize() }

    override fun deserialize() {
        spawnPoints = json.map {
            val spawn = it.value as JsonObject
            it.key to
            Location(
                Bukkit.getWorld(it.key),
                spawn["x"].toString().toDouble(),
                spawn["y"].toString().toDouble(),
                spawn["z"].toString().toDouble(),
                spawn["yaw"].toString().toFloat(),
                spawn["pitch"].toString().toFloat()
            )
        }.toMap().toMutableMap()
    }

    override fun serialize() {
        json = JsonObject(spawnPoints.map {
            it.key to
            JsonObject(mapOf(
                "x" to it.value.x,
                "y" to it.value.y,
                "z" to it.value.z,
                "pitch" to it.value.pitch,
                "yaw" to it.value.yaw
            ))
        }.toMap())
    }

    fun setSpawn(world: String, location: Location) {
        location.x = location.blockX + 0.5
        location.y = location.blockY.toDouble()
        location.z = location.blockZ + 0.5
        location.pitch = 0f
        location.yaw = roundYaw(location.yaw).toFloat()

        spawnPoints[world] = location
    }

    fun removeSpawn(world: String) {
        spawnPoints.remove(world)
    }

    fun getSpawn(world: World): Location? = spawnPoints[world.name]
}