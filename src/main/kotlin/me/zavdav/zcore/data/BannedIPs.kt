package me.zavdav.zcore.data

import com.github.cliftonlabs.json_simple.JsonArray
import com.github.cliftonlabs.json_simple.JsonObject
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Utils
import java.time.LocalDateTime
import java.util.UUID

object BannedIPs : JsonData("bannedips.json") {

    fun getEntry(ip: String): JsonObject? = json[ip] as? JsonObject

    fun getEntries(): JsonObject = json

    fun addEntry(ip: String, until: LocalDateTime?, reason: String) {
        val players = Utils.getPlayersFromIP(ip)
        json[ip] = JsonObject(mapOf(
            "uuids" to JsonArray(players.map { it.uniqueId.toString() }),
            "until" to (until ?: "forever").toString(),
            "reason" to reason
        ))

        for (player in players) {
            User.from(player).checkIsIPBanned()
        }
    }

    fun addUUID(uuid: UUID, ip: String) {
        val entry = json[ip] as? JsonObject ?: return
        val uuids = entry["uuids"] as? JsonArray ?: JsonArray()
        uuids.add(uuid.toString())
        entry["uuids"] = uuids
        json[ip] = entry
    }

    fun removeEntry(ip: String) {
        json.remove(ip)
    }
}