package me.zavdav.zcore.data

import com.github.cliftonlabs.json_simple.JsonArray
import com.github.cliftonlabs.json_simple.JsonObject
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Utils
import java.time.LocalDateTime
import java.util.UUID

object BannedIPs : JsonData("bannedips.json") {

    var entries: MutableList<IPBan> = mutableListOf()

    init { deserialize() }

    override fun deserialize() {
        entries = json.map {
            val ipBan = it.value as JsonObject
            IPBan(
                it.key,
                (ipBan["uuids"] as JsonArray).map { UUID.fromString(it.toString()) },
                ipBan["until"]?.let { LocalDateTime.parse(it.toString()) },
                ipBan["reason"].toString()
            )
        }.toMutableList()
    }

    override fun serialize() {
        json = JsonObject(entries.associate {
            it.ip to
            JsonObject(mapOf(
                "uuids" to JsonArray(it.uuids.map { it.toString() }),
                "until" to it.until?.toString(),
                "reason" to it.reason
            ))
        })
    }

    fun getEntry(ip: String): IPBan? = entries.firstOrNull { it.ip == ip }

    fun addEntry(ip: String, until: LocalDateTime?, reason: String) {
        val players = Utils.getPlayersFromIP(ip)
        entries.add(IPBan(ip, players.map { it.uniqueId }, until, reason))

        for (player in players) {
            User.from(player).checkIsIPBanned()
        }
    }

    fun addUUID(uuid: UUID, ip: String) {
        getEntry(ip)?.uuids += uuid
    }

    fun removeEntry(ip: String) {
        entries.removeIf { it.ip == ip }
    }
}