package me.zavdav.zcore.api

import me.zavdav.zcore.data.Ban
import me.zavdav.zcore.data.BannedIPs
import me.zavdav.zcore.data.IPBan
import me.zavdav.zcore.data.Mute
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.tl
import java.time.LocalDateTime
import java.util.*

object Punishments {

    fun mute(uuid: UUID) =
        mute(uuid, tl("muteReason"))

    fun mute(uuid: UUID, reason: String) =
        mute(uuid, null, reason)

    fun mute(uuid: UUID, until: LocalDateTime) =
        mute(uuid, until, tl("muteReason"))

    fun mute(uuid: UUID, until: LocalDateTime?, reason: String) {
        val user = User.from(uuid)
        user.mute = Mute(uuid, until, reason)
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
        user.ban = Ban(uuid, until, reason)
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

    fun getMute(uuid: UUID): Mute? = User.from(uuid).mute

    fun getBan(uuid: UUID): Ban? = User.from(uuid).ban

    fun getIPBan(ip: String): IPBan? = BannedIPs.getEntry(ip)

    fun getIPBan(uuid: UUID): IPBan? =
        BannedIPs.entries.firstOrNull { uuid in it.uuids }
}