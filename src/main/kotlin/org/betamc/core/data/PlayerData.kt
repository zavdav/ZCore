package org.betamc.core.data

import org.betamc.core.BMCCore
import org.bukkit.Bukkit
import org.bukkit.Location
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.LocalDateTime
import java.util.UUID

abstract class PlayerData(private val uuid: UUID) {

    private val file: File = File(BMCCore.dataFolder, "${File.separator}userdata${File.separator}$uuid.json")
    private var json: JSONObject = JSONObject()
    private var hashCode = json.hashCode()

    init {
        if (!file.exists()) {
            file.createNewFile()
            initData()
        } else {
            try {
                json = JSONParser().parse(FileReader(file)) as JSONObject
            } catch (e: ParseException) {
                BMCCore.logger.severe("${BMCCore.prefix} Could not parse player data for $uuid as it is most likely corrupt, resetting data.")
                e.printStackTrace()
                initData()
            }
        }
    }

    fun updateOnJoin(username: String) {
        json["username"] = username
        json["lastJoin"] = LocalDateTime.now().toString()
    }

    private fun initData() {
        json["uuid"] = uuid.toString()
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.uniqueId == uuid) {
                json["username"] = player.name
                json["firstJoin"] = LocalDateTime.now().toString()
                json["lastJoin"] = LocalDateTime.now().toString()
            }
        }
    }

    fun saveData() {
        if (hashCode == json.hashCode()) return
        hashCode = json.hashCode()
        FileWriter(file).use { file ->
            file.write(json.toJSONString())
            file.flush()
        }
    }

    fun getUsernameJSON(): String = json["username"].toString()

    fun addHome(name: String, location: Location) {
        val home = JSONObject()
        home["world"] = location.world.name
        home["x"] = location.blockX
        home["y"] = location.blockY
        home["z"] = location.blockZ
        home["pitch"] = location.pitch
        home["yaw"] = location.yaw

        val homes = json.getOrDefault("homes", JSONObject()) as JSONObject
        homes[name] = home
        json["homes"] = homes
    }

    fun removeHome(name: String) {
        val homes = json.getOrDefault("homes", JSONObject()) as JSONObject
        if (homes.containsKey(name)) homes.remove(name)
        json["homes"] = homes
    }

    fun homeExists(name: String): Boolean =
        getHomes().map { home -> home.lowercase() }.contains(name.lowercase())

    fun getHome(name: String): Location? {
        val home = getHomeJSON(name) ?: return null
        val world = Bukkit.getWorld(home["world"].toString()) ?: Bukkit.getWorlds()[0]
        val x = home["x"].toString().toDouble()
        val y = home["y"].toString().toDouble()
        val z = home["z"].toString().toDouble()
        val pitch = home["pitch"].toString().toFloat()
        val yaw = home["yaw"].toString().toFloat()
        return Location(world, x, y, z, yaw, pitch)
    }

    fun getHomeJSON(name: String): JSONObject? {
        val homes = json.getOrDefault("homes", JSONObject()) as JSONObject
        for (home in homes) {
            if (name.equals(home.key.toString(), true)) return home.value as JSONObject
        }
        return null
    }

    fun getFinalHomeName(name: String): String {
        val homes = json.getOrDefault("homes", JSONObject()) as JSONObject
        for (homeName in homes.keys) {
            if (name.equals(homeName.toString(), true)) return homeName.toString()
        }
        return name
    }

    fun getHomes(): List<String> =
        (json.getOrDefault("homes", JSONObject()) as JSONObject).keys.filterIsInstance<String>().toList()

    fun hasGodMode(): Boolean = json.getOrDefault("god", false) as Boolean

    fun setGodMode(god: Boolean) {
        json["god"] = god
    }
}