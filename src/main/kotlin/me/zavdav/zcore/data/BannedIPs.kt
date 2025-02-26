package me.zavdav.zcore.data

import com.github.cliftonlabs.json_simple.JsonArray
import com.github.cliftonlabs.json_simple.JsonObject
import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.getPlayersFromIP
import java.util.UUID

object BannedIPs : JsonData("bannedips.json") {

    var bannedIps: MutableMap<String, List<IPBan>> = mutableMapOf()

    init { deserialize() }

    override fun deserialize() {
        bannedIps = json.map {
            val bans = it.value as JsonArray
            it.key to
            bans.map {
                val ban = it as JsonObject
                IPBan(ban["ip"].toString(),
                    ban["issuer"]?.let { UUID.fromString(it.toString()) },
                    ban["timeIssued"].toString().toLong(),
                    ban["duration"]?.toString()?.toLong(),
                    ban["reason"].toString(),
                    ban["pardoned"] as Boolean,
                    (ban["uuids"] as JsonArray).map { UUID.fromString(it.toString()) }
                )
            }.sortedBy { it.timeIssued }
        }.toMap().toMutableMap()
    }

    override fun serialize() {
        json = JsonObject(bannedIps.map {
            it.key to
            JsonArray(it.value.map {
                JsonObject(mapOf(
                    "ip" to it.ip,
                    "issuer" to it.issuer?.toString(),
                    "timeIssued" to it.timeIssued,
                    "duration" to it.duration,
                    "reason" to it.reason,
                    "pardoned" to it.pardoned,
                    "uuids" to it.uuids.map { it.toString() }
                ))
            })
        }.toMap())
    }

    fun banIP(ip: String, issuer: UUID?, duration: Long?, reason: String) {
        val players = getPlayersFromIP(ip)
        val bans = getIPBans(ip).toMutableList()
        if (Punishments.isIPBanned(ip)) {
            bans.removeLast()
        }
        bans.add(IPBan(ip, issuer, System.currentTimeMillis(), duration, reason, false, players.map { it.uniqueId }))
        bannedIps[ip] = bans

        for (player in players) {
            User.from(player).checkIsIPBanned()
        }
    }

    fun addUUID(uuid: UUID, ip: String) {
        getIPBan(ip)?.uuids += uuid
    }

    fun unbanIP(ip: String) {
        val bans = bannedIps[ip] ?: return
        bans.lastOrNull()?.pardoned = true
        bannedIps[ip] = bans
    }

    fun getIPBans(ip: String): List<IPBan> = bannedIps[ip] ?: emptyList()

    fun getIPBan(ip: String): IPBan? = bannedIps[ip]?.lastOrNull()

    fun getIPBan(uuid: UUID): IPBan? =
        bannedIps.entries.firstOrNull { it.value.any { uuid in it.uuids } }?.value?.lastOrNull()
}