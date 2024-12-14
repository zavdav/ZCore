package org.betamc.core.data

import com.github.cliftonlabs.json_simple.JsonObject
import org.betamc.core.BMCCore
import org.betamc.core.config.Property
import org.betamc.core.util.Utils
import org.betamc.core.util.Utils.safeSubstring
import org.bukkit.entity.Player
import java.io.File
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

object BanData : JsonData(File(BMCCore.dataFolder, "bans.json")){

    private val banMap: MutableMap<UUID, Ban> = mutableMapOf()
    private val ipBanMap: MutableMap<String, Ban> = mutableMapOf()

    private var bans: JsonObject = json.getOrDefault("bans", JsonObject()) as JsonObject
    private var ipBans: JsonObject = json.getOrDefault("ipBans", JsonObject()) as JsonObject

    init {
        for (entry in bans) {
            val ban = entry.value as JsonObject
            banMap[UUID.fromString(entry.key)] = Ban(
                if (ban["until"] == "forever") null else LocalDateTime.parse(ban["until"].toString()),
                ban["reason"].toString()
            )
        }
        for (entry in ipBans) {
            val ipBan = entry.value as JsonObject
            ipBanMap[entry.key] = Ban(
                if (ipBan["until"] == "forever") null else LocalDateTime.parse(ipBan["until"].toString()),
                ipBan["reason"].toString()
            )
        }
    }

    class Ban (
        val until: LocalDateTime?,
        val reason: String
    )

    fun ban(uuid: UUID) =
        ban(uuid, Property.BAN_DEFAULT_REASON.toString())

    fun ban(uuid: UUID, reason: String) =
        ban(uuid, null, reason)

    fun ban(uuid: UUID, until: LocalDateTime) =
        ban(uuid, until, Property.BAN_DEFAULT_REASON.toString())

    fun ban(uuid: UUID, until: LocalDateTime?, reason: String) {
        banMap[uuid] = Ban(until, reason)
        bans[uuid.toString()] = JsonObject(mapOf(
            "until" to (until ?: "forever").toString(),
            "reason" to reason
        ))
        json["bans"] = bans
        when (until == null) {
            true -> Utils.getPlayerFromUUID(uuid)?.kickPlayer(
                Utils.formatColorize(Property.BAN_PERMANENT, reason).safeSubstring(0, 99))
            false -> Utils.getPlayerFromUUID(uuid)?.kickPlayer(Utils.formatColorize(Property.BAN_TEMPORARY,
                until.truncatedTo(ChronoUnit.MINUTES), reason).safeSubstring(0, 99))
        }
    }

    fun banIP(ip: String) =
        banIP(ip, Property.BAN_DEFAULT_REASON.toString())

    fun banIP(ip: String, reason: String) =
        banIP(ip, null, reason)

    fun banIP(ip: String, until: LocalDateTime) =
        banIP(ip, until, Property.BAN_DEFAULT_REASON.toString())

    fun banIP(ip: String, until: LocalDateTime?, reason: String) {
        ipBanMap[ip] = Ban(until, reason)
        ipBans[ip] = JsonObject(mapOf(
            "until" to (until ?: "forever").toString(),
            "reason" to reason
        ))
        json["ipBans"] = ipBans
        when (until == null) {
            true -> for (player in Utils.getPlayersFromIP(ip)) {
                player.kickPlayer(Utils.formatColorize(Property.IPBAN_PERMANENT, reason).safeSubstring(0, 99))
            }
            false -> for (player in Utils.getPlayersFromIP(ip)) {
                player.kickPlayer(Utils.formatColorize(Property.IPBAN_TEMPORARY,
                    until.truncatedTo(ChronoUnit.MINUTES), reason).safeSubstring(0, 99))
            }
        }
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
        val ban = ipBanMap[ip]

        if (ban == null || ban.until != null && !ban.until.isAfter(LocalDateTime.now())) {
            ipBanMap.remove(ip)
            ipBans.remove(ip)
            return false
        }
        return true
    }

    fun getBan(player: Player) = getBan(player.uniqueId)

    fun getBan(uuid: UUID): Ban? = banMap[uuid]

    fun getIPBan(player: Player): Ban? = getIPBan(player.address.address.hostAddress)

    fun getIPBan(ip: String): Ban? = ipBanMap[ip]
}