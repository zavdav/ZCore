package me.zavdav.zcore.data

import com.github.cliftonlabs.json_simple.JsonArray
import com.github.cliftonlabs.json_simple.JsonObject
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.Utils.roundTo
import org.bukkit.Bukkit
import org.bukkit.Location
import java.util.UUID

abstract class UserData protected constructor(val uuid: UUID) : JsonData("userdata/$uuid.json") {

    var username: String = "null"
    var firstJoin: Long = -1
    var lastJoin: Long = -1
    var lastSeen: Long = -1
    var playTime: Long = 0
    @Volatile
    var balance: Double = 0.0
        get() = field.coerceIn(0.0..Config.maxBalance).roundTo(2)
        set(value) { field = value.coerceIn(0.0..Config.maxBalance).roundTo(2) }
    var nickname: String? = null
    var homes: MutableMap<String, Location> = mutableMapOf()
    var ignores: MutableList<UUID> = mutableListOf()
    var mails: MutableList<String> = mutableListOf()
    var kitCooldowns: MutableMap<String, Long> = mutableMapOf()
    var seesChat: Boolean = true
    var socialSpy: Boolean = false
    var isGod: Boolean = false
    var isVanished: Boolean = false
    var mutes: MutableList<Mute> = mutableListOf()
    var bans: MutableList<Ban> = mutableListOf()
    var muteExempt: Boolean = false
    var banExempt: Boolean = false

    init { deserialize() }

    override fun deserialize() {
        json["username"]?.let { username = it.toString() }
        json["firstJoin"]?.let { firstJoin = it.toString().toLong()}
        json["lastJoin"]?.let { lastJoin = it.toString().toLong() }
        json["lastSeen"]?.let { lastSeen = it.toString().toLong() }
        json["playTime"]?.let { playTime = it.toString().toLong() }
        json["balance"]?.let { balance = it.toString().toDouble() }
        json["nickname"]?.let { nickname = it.toString() }
        json["homes"]?.let {
            homes = (it as JsonObject).map {
                val home = it.value as JsonObject
                it.key to
                Location(
                    Bukkit.getWorld(home["world"].toString()),
                    home["x"].toString().toDouble(),
                    home["y"].toString().toDouble(),
                    home["z"].toString().toDouble(),
                    home["yaw"].toString().toFloat(),
                    home["pitch"].toString().toFloat()
                )
            }.toMap().toMutableMap()
        }
        json["ignores"]?.let {
            ignores = (it as JsonArray).map { UUID.fromString(it.toString()) }.toMutableList()
        }
        json["mails"]?.let {
            mails = (it as JsonArray).map { it.toString() }.toMutableList()
        }
        json["kitCooldowns"]?.let {
            kitCooldowns = (it as JsonObject).map { it.key to it.value.toString().toLong() }.toMap().toMutableMap()
        }
        json["seesChat"]?.let { seesChat = it as Boolean }
        json["socialSpy"]?.let { socialSpy = it as Boolean }
        json["isGod"]?.let { isGod = it as Boolean }
        json["isVanished"]?.let { isVanished = it as Boolean }
        json["mutes"]?.let {
            mutes = (it as JsonArray).map {
                val mute = it as JsonObject
                Mute(uuid,
                    mute["issuer"]?.let { UUID.fromString(it.toString()) },
                    mute["timeIssued"].toString().toLong(),
                    mute["duration"]?.toString()?.toLong(),
                    mute["reason"].toString(),
                    mute["pardoned"] as Boolean
                )
            }.sortedBy { it.timeIssued }.toMutableList()
        }
        json["bans"]?.let {
            bans = (it as JsonArray).map {
                val ban = it as JsonObject
                Ban(uuid,
                    ban["issuer"]?.let { UUID.fromString(it.toString()) },
                    ban["timeIssued"].toString().toLong(),
                    ban["duration"]?.toString()?.toLong(),
                    ban["reason"].toString(),
                    ban["pardoned"] as Boolean
                )
            }.sortedBy { it.timeIssued }.toMutableList()
        }
        json["muteExempt"]?.let { muteExempt = it as Boolean }
        json["banExempt"]?.let { banExempt = it as Boolean }
    }

    override fun serialize() {
        json["username"] = username
        json["firstJoin"] = firstJoin
        json["lastJoin"] = lastJoin
        json["lastSeen"] = lastSeen
        json["playTime"] = playTime
        json["balance"] = balance
        json["nickname"] = nickname
        json["homes"] = JsonObject(homes.map {
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
        json["ignores"] = JsonArray(ignores.map { it.toString() })
        json["mails"] = JsonArray(mails)
        json["kitCooldowns"] = JsonObject(kitCooldowns)
        json["seesChat"] = seesChat
        json["socialSpy"] = socialSpy
        json["isGod"] = isGod
        json["isVanished"] = isVanished
        json["mutes"] = JsonArray(mutes.map {
            JsonObject(mapOf(
                "uuid" to uuid.toString(),
                "issuer" to it.issuer?.toString(),
                "timeIssued" to it.timeIssued,
                "duration" to it.duration,
                "reason" to it.reason,
                "pardoned" to it.pardoned
            ))
        })
        json["bans"] = JsonArray(bans.map {
            JsonObject(mapOf(
                "uuid" to uuid.toString(),
                "issuer" to it.issuer?.toString(),
                "timeIssued" to it.timeIssued,
                "duration" to it.duration,
                "reason" to it.reason,
                "pardoned" to it.pardoned
            ))
        })
        json["muteExempt"] = muteExempt
        json["banExempt"] = banExempt
    }

    fun updateOnJoin(username: String) {
        val now = System.currentTimeMillis()
        this.username = username
        lastJoin = now
        lastSeen = now
    }

    fun addHome(name: String, location: Location) {
        homes[name] = location
    }

    fun removeHome(name: String) {
        homes.entries.removeIf { it.key.equals(name, true) }
    }

    fun homeExists(name: String): Boolean = getHome(name) != null

    fun getHomeLocation(name: String): Location? {
        val location = getHome(name) ?: return null
        location.y = Utils.getSafeHeight(location).toDouble()
        return location
    }

    fun getHome(name: String): Location? =
        homes.entries.firstOrNull { it.key.equals(name, true) }?.value

    fun getHomeName(name: String): String =
        homes.keys.firstOrNull { it.equals(name, true) } ?: name

    fun getHomes(): List<String> = homes.map { it.key }

    fun setIgnored(uuid: UUID, ignore: Boolean) =
        if (ignore) ignores.add(uuid) else ignores.remove(uuid)

    fun addMail(name: String, message: String) =
        mails.add("$name: $message")

    fun clearMail() = mails.clear()

    fun addKitCooldown(kit: Kit, cooldown: Int) {
        kitCooldowns[kit.name] = System.currentTimeMillis() + cooldown * 1000
    }
}