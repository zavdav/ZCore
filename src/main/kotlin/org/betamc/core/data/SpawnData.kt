package org.betamc.core.data

import org.betamc.core.BMCCore
import org.betamc.core.util.Utils
import org.bukkit.Location
import org.bukkit.World
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object SpawnData {

    private val file: File = File(BMCCore.dataFolder, "spawns.json")
    private var json: JSONObject = JSONObject()
    private var hashCode = json.hashCode()

    init {
        if (!file.exists()) {
            file.createNewFile()
        } else {
            try {
                json = JSONParser().parse(FileReader(file)) as JSONObject
            } catch (e: ParseException) {
                BMCCore.logger.severe("${BMCCore.prefix} Could not parse spawn data as it is most likely corrupt, resetting data.")
                e.printStackTrace()
            }
        }
    }

    fun setSpawn(world: String, location: Location) {
        val spawn = JSONObject()
        spawn["x"] = location.blockX + 0.5
        spawn["y"] = location.blockY
        spawn["z"] = location.blockZ + 0.5
        spawn["pitch"] = 0
        spawn["yaw"] = Utils.roundYaw(location.yaw)

        json[world] = spawn
    }

    fun removeSpawn(world: String) = json.remove(world)

    fun getSpawn(world: World): Location? {
        val spawn = (json[world.name] ?: return null) as JSONObject
        val x = spawn["x"].toString().toDouble()
        val y = spawn["y"].toString().toDouble()
        val z = spawn["z"].toString().toDouble()
        val pitch = spawn["pitch"].toString().toFloat()
        val yaw = spawn["yaw"].toString().toFloat()

        return Location(world, x, y, z, yaw, pitch)
    }

    fun saveData() {
        if (hashCode == json.hashCode()) return
        hashCode = json.hashCode()
        FileWriter(file).use { file ->
            file.write(json.toJSONString())
            file.flush()
        }
    }
}