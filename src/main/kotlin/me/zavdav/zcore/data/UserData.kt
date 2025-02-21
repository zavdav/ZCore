package me.zavdav.zcore.data

import com.github.cliftonlabs.json_simple.JsonArray
import com.github.cliftonlabs.json_simple.JsonObject
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.config.Kits
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.Utils.roundTo
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.*
import kotlin.math.floor

abstract class UserData protected constructor(val uuid: UUID) : JsonData("userdata/$uuid.json") {

    protected var username: String
        get() = json["username", ""]
        private set(value) { json["username"] = value }

    var firstJoin: Long
        get() = json["firstJoin", 0L]
        set(value) { json["firstJoin"] = value }

    var lastJoin: Long
        get() = json["lastJoin", 0L]
        set(value) { json["lastJoin"] = value }

    var lastSeen: Long
        get() = json["lastSeen", 0L]
        set(value) { json["lastSeen"] = value }

    var playTime: Long
        get() = json["playTime", 0L]
        set(value) { json["playTime"] = value }

    var balance: Double
        get() = json["balance", 0.0].coerceIn(0.0..Config.maxBalance).roundTo(2)
        set(value) { json["balance"] = value.coerceIn(0.0..Config.maxBalance).roundTo(2) }

    var nickname: String
        get() = json["nickname", username]
        set(value) { json["nickname"] = value }

    var homes: JsonObject
        get() = json["homes", JsonObject()]
        set(value) { json["homes"] = value }

    var ignores: Set<UUID>
        get() = json["ignores", JsonArray()].map { UUID.fromString(it.toString()) }.toSet()
        set(value) { json["ignores"] = JsonArray(value.map { it.toString() }) }

    var mails: List<String>
        @Suppress("UNCHECKED_CAST")
        get() = json["mails", JsonArray()].toList() as List<String>
        set(value) { json["mails"] = JsonArray(value) }

    var seesChat: Boolean
        get() = json["seesChat", true]
        set(value) { json["seesChat"] = value }

    var socialSpy: Boolean
        get() = json["socialSpy", false]
        set(value) { json["socialSpy"] = value }

    var isGod: Boolean
        get() = json["isGod", false]
        set(value) { json["isGod"] = value }

    var vanished: Boolean
        get() = json["vanished", false]
        set(value) { json["vanished"] = value }

    var ban: JsonObject?
        get() = json["ban", null]
        set(value) { if (value != null) json["ban"] = value else json.remove("ban") }

    var mute: JsonObject?
        get() = json["mute", null]
        set(value) { if (value != null) json["mute"] = value else json.remove("mute") }

    var banExempt: Boolean
        get() = json["banExempt", false]
        set(value) { json["banExempt"] = value }

    var muteExempt: Boolean
        get() = json["muteExempt", false]
        set(value) { json["muteExempt"] = value }

    var kitCooldowns: Map<Kits.Kit, Long>
        get() = json["kitCooldowns", emptyMap<String, Long>()].entries
            .associate { Kits.getKit(it.key)!! to it.value }
        set(value) {
            json["kitCooldowns"] = JsonObject(value.entries
                .associate { it.key.name to it.value })
        }

    init {
        if (initialize) initData()
    }

    private fun initData() {
        json["uuid"] = uuid.toString()
        val player = Bukkit.getOnlinePlayers().firstOrNull { it.uniqueId == uuid }
        val now = System.currentTimeMillis()
        username = player?.name ?: ""
        firstJoin = now
        lastJoin = now
        lastSeen = now
    }

    fun updateOnJoin(username: String) {
        val now = System.currentTimeMillis()
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

        val homes = this.homes
        homes[name] = home
        this.homes = homes
    }

    fun removeHome(name: String) {
        val homes = this.homes
        if (name in homes.keys) homes.remove(name)
        this.homes = homes
    }

    fun homeExists(name: String): Boolean = getHome(name) != null

    fun getHomeLocation(name: String): Location? {
        val home = getHome(name) ?: return null
        val world = Bukkit.getWorld(home["world"].toString())
        val x = floor(home["x"].toString().toDouble()) + 0.5
        var y = home["y"].toString().toDouble()
        val z = floor(home["z"].toString().toDouble()) + 0.5
        y = Utils.getSafeHeight(Location(world, x, y, z)).toDouble()
        val pitch = home["pitch"].toString().toFloat()
        val yaw = home["yaw"].toString().toFloat()
        return Location(world, x, y, z, yaw, pitch)
    }

    fun getHome(name: String): JsonObject? {
        for (home in homes) {
            if (name.equals(home.key, true)) return home.value as JsonObject
        }
        return null
    }

    fun getHomeName(name: String): String {
        for (homeName in homes.keys) {
            if (name.equals(homeName, true)) return homeName
        }
        return name
    }

    fun getHomes(): List<String> =
        homes.keys.filterIsInstance<String>().toList()

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
        mails = emptyList()
    }

    fun addKitCooldown(kit: Kits.Kit, cooldown: Int) {
        val kitCooldowns = kitCooldowns.toMutableMap()
        kitCooldowns[kit] = System.currentTimeMillis() + cooldown * 1000
        this.kitCooldowns = kitCooldowns
    }
}