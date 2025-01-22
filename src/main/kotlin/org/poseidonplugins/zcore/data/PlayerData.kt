package org.poseidonplugins.zcore.data

import com.github.cliftonlabs.json_simple.JsonArray
import com.github.cliftonlabs.json_simple.JsonObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.roundTo
import java.io.File
import java.time.LocalDateTime
import java.util.UUID
import kotlin.math.floor

abstract class PlayerData(val uuid: UUID) : JsonData(
    File(ZCore.dataFolder, "${File.separator}userdata${File.separator}$uuid.json")) {

    protected var username: String
        get() = json["username"].toString()
        private set(value) { json["username"] = value }

    var firstJoin: LocalDateTime
        get() = LocalDateTime.parse(json["firstJoin"].toString())
        set(value) { json["firstJoin"] = value.toString() }

    var lastJoin: LocalDateTime
        get() = LocalDateTime.parse(json["lastJoin"].toString())
        set(value) { json["lastJoin"] = value.toString() }

    var lastSeen: LocalDateTime
        get() = LocalDateTime.parse(json["lastSeen"].toString())
        set(value) { json["lastSeen"] = value.toString() }

    var balance: Double
        get() = json["balance"].toString().toDouble().roundTo(2)
        set(value) { json["balance"] = value }

    var nickname: String
        get() = if ("nickname" in json.keys) json["nickname"].toString() else username
        set(value) { json["nickname"] = value }

    var ignores: Set<UUID>
        get() { return ((json["ignores"] ?: return setOf()) as JsonArray)
                .map { UUID.fromString(it.toString()) }.toSet() }
        set(value) { json["ignores"] = JsonArray(value.map { it.toString() }) }

    var mails: List<String>
        get() = (json.getOrDefault("mails", JsonArray()) as JsonArray).toList() as List<String>
        set(value) { json["mails"] = JsonArray(value) }

    var seesChat: Boolean
        get() = json.getOrDefault("seesChat", true) as Boolean
        set(value) { json["seesChat"] = value }

    var isGod: Boolean
        get() = json.getOrDefault("isGod", false) as Boolean
        set(value) { json["isGod"] = value }

    var vanished: Boolean
        get() = json.getOrDefault("vanished", false) as Boolean
        set(value) { json["vanished"] = value }

    init {
        if (initialize) initData()
        balance = balance.coerceAtMost(Config.getDouble("maxBalance", 0.0, 10000000000000.0))
    }

    private fun initData() {
        json["uuid"] = uuid.toString()
        balance = 0.0
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.uniqueId == uuid) {
                val now = LocalDateTime.now()
                username = player.name
                firstJoin = now
                lastJoin = now
                lastSeen = now
            }
        }
    }

    fun updateOnJoin(username: String) {
        val now = LocalDateTime.now()
        this.username = username
        lastJoin = now
        lastSeen = now
    }

    fun addHome(name: String, location: Location) {
        val home = JsonObject()
        home["world"] = location.world.name
        home["x"] = location.blockX
        home["y"] = location.blockY
        home["z"] = location.blockZ
        home["pitch"] = location.pitch
        home["yaw"] = location.yaw

        val homes = json.getOrDefault("homes", JsonObject()) as JsonObject
        homes[name] = home
        json["homes"] = homes
    }

    fun removeHome(name: String) {
        val homes = json.getOrDefault("homes", JsonObject()) as JsonObject
        if (name in homes.keys) homes.remove(name)
        json["homes"] = homes
    }

    fun homeExists(name: String): Boolean = getHomeJson(name) != null

    fun getHome(name: String): Location? {
        val home = getHomeJson(name) ?: return null
        val world = Bukkit.getWorld(home["world"].toString())
        val x = floor(home["x"].toString().toDouble()) + 0.5
        var y = home["y"].toString().toDouble()
        val z = floor(home["z"].toString().toDouble()) + 0.5
        y = Utils.getSafeHeight(Location(world, x, y, z)).toDouble()
        val pitch = home["pitch"].toString().toFloat()
        val yaw = home["yaw"].toString().toFloat()
        return Location(world, x, y, z, yaw, pitch)
    }

    fun getHomeJson(name: String): JsonObject? {
        val homes = (json["homes"] ?: return null) as JsonObject
        for (home in homes) {
            if (name.equals(home.key, true)) return home.value as JsonObject
        }
        return null
    }

    fun getFinalHomeName(name: String): String {
        val homes = (json["homes"] ?: return name) as JsonObject
        for (homeName in homes.keys) {
            if (name.equals(homeName, true)) return homeName
        }
        return name
    }

    fun getHomes(): List<String> =
        (json.getOrDefault("homes", JsonObject()) as JsonObject).keys.filterIsInstance<String>().toList()

    fun resetNickname() = json.remove("nickname")

    fun setIgnored(uuid: UUID, ignore: Boolean) {
        val ignores = ignores.toMutableSet()
        if (ignore) ignores.add(uuid) else ignores.remove(uuid)
        this.ignores = ignores
    }

    fun addMail(name: String, message: String) {
        mails += "$name: $message"
    }

    fun clearMail() {
        mails = listOf()
    }
}