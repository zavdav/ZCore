package org.poseidonplugins.zcore.data

import com.github.cliftonlabs.json_simple.JsonObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.util.Utils
import java.io.File

object WarpData : JsonData(File(ZCore.dataFolder, "warps.json")) {

    fun setWarp(name: String, location: Location) {
        val warp = JsonObject()
        warp["world"] = location.world.name
        warp["x"] = location.blockX + 0.5
        warp["y"] = location.blockY
        warp["z"] = location.blockZ + 0.5
        warp["pitch"] = 0
        warp["yaw"] = Utils.roundYaw(location.yaw)

        json[name] = warp
    }

    fun removeWarp(name: String) = json.remove(name)

    fun getWarps(): List<String> = json.keys.filterIsInstance<String>().toList()

    fun getWarp(name: String): Location? {
        val warp = getWarpJson(name) ?: return null
        val world = Bukkit.getWorld(warp["world"].toString())
        val x = warp["x"].toString().toDouble()
        var y = warp["y"].toString().toDouble()
        val z = warp["z"].toString().toDouble()
        y = Utils.getSafeHeight(Location(world, x, y, z)).toDouble()
        val pitch = warp["pitch"].toString().toFloat()
        val yaw = warp["yaw"].toString().toFloat()

        return Location(world, x, y, z, yaw, pitch)
    }

    fun getWarpJson(name: String): JsonObject? {
        for (warp in json) {
            if (name.equals(warp.key, true)) return warp.value as JsonObject
        }
        return null
    }

    fun warpExists(name: String): Boolean = getWarpJson(name) != null

    fun getFinalWarpName(name: String): String {
        for (warpName in json.keys) {
            if (name.equals(warpName, true)) return warpName
        }
        return name
    }
}