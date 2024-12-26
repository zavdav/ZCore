package org.poseidonplugins.zcore.data

import com.github.cliftonlabs.json_simple.JsonObject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.config.Property
import org.poseidonplugins.zcore.util.Utils
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
        get() = json["balance"].toString().toDouble()
        set(value) { json["balance"] = value }

    val isBanned: Boolean
        get() = BanData.isBanned(uuid)

    var isGod: Boolean
        get() = json.getOrDefault("god", false) as Boolean
        set(value) { json["god"] = value }

    var vanished: Boolean
        get() = json.getOrDefault("vanish", false) as Boolean
        set(value) { json["vanish"] = value }

    init {
        if (initialize) initData()
        balance = balance.coerceAtMost(Property.MAX_BALANCE.toDouble())
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

    fun updateOnQuit() {
        lastSeen = LocalDateTime.now()
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
        if (homes.containsKey(name)) homes.remove(name)
        json["homes"] = homes
    }

    fun homeExists(name: String): Boolean =
        getHomes().map { home -> home.lowercase() }.contains(name.lowercase())

    fun getHome(name: String): Location? {
        val home = getHomeJSON(name) ?: return null
        val world = Bukkit.getWorld(home["world"].toString()) ?: Bukkit.getWorlds()[0]
        val x = floor(home["x"].toString().toDouble()) + 0.5
        var y = home["y"].toString().toDouble()
        val z = floor(home["z"].toString().toDouble()) + 0.5
        y = Utils.getSafeHeight(Location(world, x, y, z)).toDouble()
        val pitch = home["pitch"].toString().toFloat()
        val yaw = home["yaw"].toString().toFloat()
        return Location(world, x, y, z, yaw, pitch)
    }

    fun getHomeJSON(name: String): JsonObject? {
        val homes = json.getOrDefault("homes", JsonObject()) as JsonObject
        for (home in homes) {
            if (name.equals(home.key.toString(), true)) return home.value as JsonObject
        }
        return null
    }

    fun getFinalHomeName(name: String): String {
        val homes = json.getOrDefault("homes", JsonObject()) as JsonObject
        for (homeName in homes.keys) {
            if (name.equals(homeName.toString(), true)) return homeName.toString()
        }
        return name
    }

    fun getHomes(): List<String> =
        (json.getOrDefault("homes", JsonObject()) as JsonObject).keys.filterIsInstance<String>().toList()
}