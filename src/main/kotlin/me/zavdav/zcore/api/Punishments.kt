package me.zavdav.zcore.api

import com.github.cliftonlabs.json_simple.JsonArray
import com.github.cliftonlabs.json_simple.JsonObject
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.data.JsonData
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.tl
import org.bukkit.entity.Player
import java.io.File
import java.time.LocalDateTime
import java.util.*

object Punishments : JsonData(File(ZCore.dataFolder, "punishments.json")) {

    private val muteMap: MutableMap<UUID, Mute> = mutableMapOf()
    private val banMap: MutableMap<UUID, Ban> = mutableMapOf()
    private val ipBanMap: MutableMap<String, IPBan> = mutableMapOf()

    private var mutes: JsonObject = json["mutes", JsonObject()]
    private var bans: JsonObject = json["bans", JsonObject()]
    private var ipBans: JsonObject = json["ipBans", JsonObject()]

    init {
        for (entry in mutes) {
            val mute = entry.value as JsonObject
            val uuid = UUID.fromString(entry.key)
            muteMap[uuid] = Mute(
                uuid,
                if (mute["until"] == "forever") null else LocalDateTime.parse(mute["until"].toString()),
                mute["reason"].toString()
            )
        }
        for (entry in bans) {
            val ban = entry.value as JsonObject
            val uuid = UUID.fromString(entry.key)
            banMap[uuid] = Ban(
                uuid,
                if (ban["until"] == "forever") null else LocalDateTime.parse(ban["until"].toString()),
                ban["reason"].toString()
            )
        }
        for (entry in ipBans) {
            val ipBan = entry.value as JsonObject
            ipBanMap[entry.key] = IPBan(
                entry.key,
                (ipBan["uuids"] as JsonArray).map { obj -> UUID.fromString(obj.toString()) }.toSet(),
                if (ipBan["until"] == "forever") null else LocalDateTime.parse(ipBan["until"].toString()),
                ipBan["reason"].toString()
            )
        }
    }

    class Mute(
        val uuid: UUID,
        val until: LocalDateTime?,
        val reason: String
    )

    class Ban(
        val uuid: UUID,
        val until: LocalDateTime?,
        val reason: String
    )

    class IPBan(
        val ip: String,
        uuids: Set<UUID>,
        val until: LocalDateTime?,
        val reason: String
    ) {
        var uuids = uuids; private set

        fun addUUID(uuid: UUID) {
            uuids = uuids.plus(uuid)
            ipBanMap[ip] = this
            ipBans[ip] = JsonObject(mapOf(
                "uuids" to JsonArray(uuids.map { entry -> entry.toString() }),
                "until" to (until ?: "forever").toString(),
                "reason" to reason
            ))
            json["ipBans"] = ipBans
        }
    }

    fun mute(uuid: UUID) =
        mute(uuid, tl("muteReason"))

    fun mute(uuid: UUID, reason: String) =
        mute(uuid, null, reason)

    fun mute(uuid: UUID, until: LocalDateTime) =
        mute(uuid, until, tl("muteReason"))

    fun mute(uuid: UUID, until: LocalDateTime?, reason: String) {
        muteMap[uuid] = Mute(uuid, until, reason)
        mutes[uuid.toString()] = JsonObject(mapOf(
            "until" to (until ?: "forever").toString(),
            "reason" to reason
        ))
        json["mutes"] = mutes

        User.from(uuid).checkIsMuted()
    }

    fun ban(uuid: UUID) =
        ban(uuid, tl("banReason"))

    fun ban(uuid: UUID, reason: String) =
        ban(uuid, null, reason)

    fun ban(uuid: UUID, until: LocalDateTime) =
        ban(uuid, until, tl("banReason"))

    fun ban(uuid: UUID, until: LocalDateTime?, reason: String) {
        banMap[uuid] = Ban(uuid, until, reason)
        bans[uuid.toString()] = JsonObject(mapOf(
            "until" to (until ?: "forever").toString(),
            "reason" to reason
        ))
        json["bans"] = bans

        User.from(uuid).checkIsBanned()
    }

    fun banIP(ip: String) =
        banIP(ip, tl("banReason"))

    fun banIP(ip: String, reason: String) =
        banIP(ip, null, reason)

    fun banIP(ip: String, until: LocalDateTime) =
        banIP(ip, until, tl("banReason"))

    fun banIP(ip: String, until: LocalDateTime?, reason: String) {
        val players = Utils.getPlayersFromIP(ip)
        ipBanMap[ip] = IPBan(ip, players.map { it.uniqueId }.toSet(), until, reason)
        ipBans[ip] = JsonObject(mapOf(
            "uuids" to JsonArray(players.map { it.uniqueId.toString() }),
            "until" to (until ?: "forever").toString(),
            "reason" to reason
        ))
        json["ipBans"] = ipBans

        for (player in players) {
            User.from(player).checkIsIPBanned()
        }
    }

    fun unmute(uuid: UUID) {
        muteMap.remove(uuid)
        mutes.remove(uuid.toString())
        json["mutes"] = mutes
    }

    fun unban(uuid: UUID) {
        banMap.remove(uuid)
        bans.remove(uuid.toString())
        json["bans"] = bans
    }

    fun unbanIP(ip: String) {
        ipBanMap.remove(ip)
        ipBans.remove(ip)
        json["ipBans"] = ipBans
    }

    fun isMuted(uuid: UUID): Boolean {
        val mute = muteMap[uuid]

        if (mute == null || mute.until != null && !mute.until.isAfter(LocalDateTime.now())) {
            muteMap.remove(uuid)
            mutes.remove(uuid.toString())
            return false
        }
        return true
    }

    fun isBanned(uuid: UUID): Boolean {
        val ban = banMap[uuid]

        if (ban == null || ban.until != null && !ban.until.isAfter(LocalDateTime.now())) {
            banMap.remove(uuid)
            bans.remove(uuid.toString())
            return false
        }
        return true
    }

    fun isIPBanned(ip: String): Boolean {
        val ipBan = ipBanMap[ip]

        if (ipBan == null || ipBan.until != null && !ipBan.until.isAfter(LocalDateTime.now())) {
            ipBanMap.remove(ip)
            ipBans.remove(ip)
            return false
        }
        return true
    }

    fun getMute(player: Player): Mute? = getMute(player.uniqueId)

    fun getMute(uuid: UUID): Mute? = muteMap[uuid]

    fun getBan(player: Player): Ban? = getBan(player.uniqueId)

    fun getBan(uuid: UUID): Ban? = banMap[uuid]

    fun getIPBan(player: Player): IPBan? = getIPBan(player.address.address.hostAddress)

    fun getIPBan(uuid: UUID): IPBan? =
        ipBanMap.entries.firstOrNull { entry -> uuid in entry.value.uuids }?.value

    fun getIPBan(ip: String): IPBan? = ipBanMap[ip]
}