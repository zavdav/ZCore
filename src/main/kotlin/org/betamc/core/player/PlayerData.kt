package org.betamc.core.player

import org.betamc.core.BMCCore
import org.bukkit.Bukkit
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

    fun getGodStatus(): Boolean = json.getOrDefault("god", false) as Boolean

    fun setGodStatus(status: Boolean) {
        json["god"] = status
    }
}