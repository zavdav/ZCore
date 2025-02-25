package me.zavdav.zcore.api

import me.zavdav.zcore.data.Ban
import me.zavdav.zcore.data.BannedIPs
import me.zavdav.zcore.data.IPBan
import me.zavdav.zcore.data.Mute
import me.zavdav.zcore.user.User
import java.util.UUID

object Punishments {

    @JvmStatic
    fun mutePlayer(uuid: UUID, issuer: UUID?, duration: Long?, reason: String) {
        val user = User.from(uuid)
        if (isPlayerMuted(uuid)) {
            user.mutes.removeLast()
        }
        user.mutes.add(Mute(uuid, issuer, System.currentTimeMillis(), duration, reason, false))
        user.checkIsMuted()
    }

    @JvmStatic
    fun banPlayer(uuid: UUID, issuer: UUID?, duration: Long?, reason: String) {
        val user = User.from(uuid)
        if (isPlayerBanned(uuid)) {
            user.bans.removeLast()
        }
        user.bans.add(Ban(uuid, issuer, System.currentTimeMillis(), duration, reason, false))
        user.checkIsBanned()
    }

    @JvmStatic
    fun banIP(ip: String, issuer: UUID?, duration: Long?, reason: String) {
        BannedIPs.banIP(ip, issuer, duration, reason)
    }

    @JvmStatic
    fun unmutePlayer(uuid: UUID) {
        if (!isPlayerMuted(uuid)) return
        User.from(uuid).mutes.lastOrNull()?.pardoned = true
    }

    @JvmStatic
    fun unbanPlayer(uuid: UUID) {
        if (!isPlayerBanned(uuid)) return
        User.from(uuid).bans.lastOrNull()?.pardoned = true
    }

    @JvmStatic
    fun unbanIP(ip: String) {
        if (!isIPBanned(ip)) return
        BannedIPs.unbanIP(ip)
    }

    @JvmStatic
    fun isPlayerMuted(uuid: UUID): Boolean {
        val mute = getMute(uuid) ?: return false
        return if (mute.pardoned) false
        else when {
            mute.duration == null -> true
            System.currentTimeMillis() <= mute.timeIssued + mute.duration * 1000 -> true
            else -> false
        }
    }

    @JvmStatic
    fun isPlayerBanned(uuid: UUID): Boolean {
        val ban = getBan(uuid) ?: return false
        return if (ban.pardoned) false
        else when {
            ban.duration == null -> true
            System.currentTimeMillis() <= ban.timeIssued + ban.duration * 1000 -> true
            else -> false
        }
    }

    @JvmStatic
    fun isIPBanned(ip: String): Boolean {
        val ipBan = getIPBan(ip) ?: return false
        return if (ipBan.pardoned) false
        else when {
            ipBan.duration == null -> true
            System.currentTimeMillis() <= ipBan.timeIssued + ipBan.duration * 1000 -> true
            else -> false
        }
    }

    @JvmStatic
    fun getMutes(uuid: UUID): List<Mute> = User.from(uuid).mutes

    @JvmStatic
    fun getMute(uuid: UUID): Mute? = User.from(uuid).mutes.lastOrNull()

    @JvmStatic
    fun getBans(uuid: UUID): List<Ban> = User.from(uuid).bans

    @JvmStatic
    fun getBan(uuid: UUID): Ban? = User.from(uuid).bans.lastOrNull()

    @JvmStatic
    fun getIPBans(ip: String): List<IPBan> = BannedIPs.getIPBans(ip)

    @JvmStatic
    fun getIPBan(ip: String): IPBan? = BannedIPs.getIPBan(ip)

    @JvmStatic
    fun getIPBan(uuid: UUID): IPBan? = BannedIPs.getIPBan(uuid)
}