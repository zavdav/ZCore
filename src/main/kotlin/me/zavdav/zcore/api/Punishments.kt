package me.zavdav.zcore.api

import com.github.cliftonlabs.json_simple.JsonArray
import com.github.cliftonlabs.json_simple.JsonObject
import me.zavdav.zcore.data.BannedIPs
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.tl
import java.time.LocalDateTime
import java.util.*

object Punishments {

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
        val uuids: Set<UUID>,
        val until: LocalDateTime?,
        val reason: String
    )

    fun mute(uuid: UUID) =
        mute(uuid, tl("muteReason"))

    fun mute(uuid: UUID, reason: String) =
        mute(uuid, null, reason)

    fun mute(uuid: UUID, until: LocalDateTime) =
        mute(uuid, until, tl("muteReason"))

    fun mute(uuid: UUID, until: LocalDateTime?, reason: String) {
        val user = User.from(uuid)
        user.mute = JsonObject(mapOf(
            "until" to (until ?: "forever").toString(),
            "reason" to reason
        ))

        user.checkIsMuted()
    }

    fun ban(uuid: UUID) =
        ban(uuid, tl("banReason"))

    fun ban(uuid: UUID, reason: String) =
        ban(uuid, null, reason)

    fun ban(uuid: UUID, until: LocalDateTime) =
        ban(uuid, until, tl("banReason"))

    fun ban(uuid: UUID, until: LocalDateTime?, reason: String) {
        val user = User.from(uuid)
        user.ban = JsonObject(mapOf(
            "until" to (until ?: "forever").toString(),
            "reason" to reason
        ))

        user.checkIsBanned()
    }

    fun banIP(ip: String) =
        banIP(ip, tl("banReason"))

    fun banIP(ip: String, reason: String) =
        banIP(ip, null, reason)

    fun banIP(ip: String, until: LocalDateTime) =
        banIP(ip, until, tl("banReason"))

    fun banIP(ip: String, until: LocalDateTime?, reason: String) {
        BannedIPs.addEntry(ip, until, reason)
    }

    fun unmute(uuid: UUID) {
        User.from(uuid).mute = null
    }

    fun unban(uuid: UUID) {
        User.from(uuid).ban = null
    }

    fun unbanIP(ip: String) {
        BannedIPs.removeEntry(ip)
    }

    fun isMuted(uuid: UUID): Boolean {
        val mute = getMute(uuid) ?: return false
        if (mute.until != null && !mute.until.isAfter(LocalDateTime.now())) {
            unmute(uuid)
            return false
        }
        return true
    }

    fun isBanned(uuid: UUID): Boolean {
        val ban = getBan(uuid) ?: return false
        if (ban.until != null && !ban.until.isAfter(LocalDateTime.now())) {
            unban(uuid)
            return false
        }
        return true
    }

    fun isIPBanned(ip: String): Boolean {
        val ipBan = getIPBan(ip) ?: return false

        if (ipBan.until != null && !ipBan.until.isAfter(LocalDateTime.now())) {
            unbanIP(ip)
            return false
        }
        return true
    }

    fun getMute(uuid: UUID): Mute? {
        val user = User.from(uuid)
        val mute = user.mute ?: return null

        return try {
            val until = if (mute["until"] == "forever") null
            else LocalDateTime.parse(mute["until"].toString())
            val reason = mute["reason"].toString()
            Mute(uuid, until, reason)
        } catch (_: Exception) {
            null
        }
    }

    fun getBan(uuid: UUID): Ban? {
        val user = User.from(uuid)
        val ban = user.ban ?: return null

        return try {
            val until = if (ban["until"] == "forever") null
            else LocalDateTime.parse(ban["until"].toString())
            val reason = ban["reason"].toString()
            Ban(uuid, until, reason)
        } catch (_: Exception) {
            null
        }
    }

    fun getIPBan(uuid: UUID): IPBan? {
        val entry = BannedIPs.getEntries().entries.firstOrNull {
            uuid.toString() in (it.value as JsonObject)["uuids"] as JsonArray
        } ?: return null
        return getIPBan(entry.key)
    }

    fun getIPBan(ip: String): IPBan? {
        val ipBan = BannedIPs.getEntry(ip) ?: return null

        return try {
            val uuids = (ipBan["uuids"] as? JsonArray)
                ?.map { UUID.fromString(it.toString()) }?.toSet() ?: emptySet<UUID>()
            val until = if (ipBan["until"] == "forever") null
            else LocalDateTime.parse(ipBan["until"].toString())
            val reason = ipBan["reason"].toString()
            IPBan(ip, uuids, until, reason)
        } catch (_: Exception) {
            null
        }
    }
}