package org.betamc.core.data

import com.github.cliftonlabs.json_simple.JsonObject
import org.betamc.core.BMCCore
import org.betamc.core.util.Utils
import org.bukkit.Location
import org.bukkit.World
import java.io.File

object SpawnData : JsonData(File(BMCCore.dataFolder, "spawns.json")) {

    fun setSpawn(world: String, location: Location) {
        val spawn = JsonObject()
        spawn["x"] = location.blockX + 0.5
        spawn["y"] = location.blockY
        spawn["z"] = location.blockZ + 0.5
        spawn["pitch"] = 0
        spawn["yaw"] = Utils.roundYaw(location.yaw)

        json[world] = spawn
    }

    fun removeSpawn(world: String) = json.remove(world)

    fun getSpawn(world: World): Location? {
        val spawn = (json[world.name] ?: return null) as JsonObject
        val x = spawn["x"].toString().toDouble()
        val y = spawn["y"].toString().toDouble()
        val z = spawn["z"].toString().toDouble()
        val pitch = spawn["pitch"].toString().toFloat()
        val yaw = spawn["yaw"].toString().toFloat()

        return Location(world, x, y, z, yaw, pitch)
    }
}