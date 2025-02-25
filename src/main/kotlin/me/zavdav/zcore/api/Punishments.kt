package me.zavdav.zcore.api

import me.zavdav.zcore.data.Ban
import me.zavdav.zcore.data.BannedIPs
import me.zavdav.zcore.data.IPBan
import me.zavdav.zcore.data.Mute
import me.zavdav.zcore.user.User
import java.util.UUID

/**
 * This class provides methods to access ZCore's punishment system.
 */
object Punishments {

    /**
     * Mutes a player for a specified duration and reason.
     * If the player is already muted, the current mute will be overwritten.
     *
     * @param uuid the player's UUID
     * @param issuer the player who issued the mute (optional)
     * @param duration the duration of the mute. If null, the mute is permanent.
     * @param reason the reason for the mute
     */
    @JvmStatic
    fun mutePlayer(uuid: UUID, issuer: UUID?, duration: Long?, reason: String) {
        val user = User.from(uuid)
        if (isPlayerMuted(uuid)) {
            user.mutes.removeLast()
        }
        user.mutes.add(Mute(uuid, issuer, System.currentTimeMillis(), duration, reason, false))
        user.checkIsMuted()
    }

    /**
     * Bans a player for a specified duration and reason.
     * If the player is already banned, the current ban will be overwritten.
     *
     * @param uuid the player's UUID
     * @param issuer the player who issued the ban (optional)
     * @param duration the duration of the ban. If null, the ban is permanent.
     * @param reason the reason for the ban
     */
    @JvmStatic
    fun banPlayer(uuid: UUID, issuer: UUID?, duration: Long?, reason: String) {
        val user = User.from(uuid)
        if (isPlayerBanned(uuid)) {
            user.bans.removeLast()
        }
        user.bans.add(Ban(uuid, issuer, System.currentTimeMillis(), duration, reason, false))
        user.checkIsBanned()
    }

    /**
     * Bans an IP address for a specified duration and reason.
     * If the IP is already banned, the current ban will be overwritten.
     *
     * @param ip the IP address
     * @param issuer the player who issued the ban (optional)
     * @param duration the duration of the ban. If null, the ban is permanent.
     * @param reason the reason for the ban
     */
    @JvmStatic
    fun banIP(ip: String, issuer: UUID?, duration: Long?, reason: String) {
        BannedIPs.banIP(ip, issuer, duration, reason)
    }

    /**
     * Unmutes a player if they are muted.
     *
     * @param uuid the player's UUID
     */
    @JvmStatic
    fun unmutePlayer(uuid: UUID) {
        if (!isPlayerMuted(uuid)) return
        User.from(uuid).mutes.lastOrNull()?.pardoned = true
    }

    /**
     * Unbans a player if they are banned.
     *
     * @param uuid the player's UUID
     */
    @JvmStatic
    fun unbanPlayer(uuid: UUID) {
        if (!isPlayerBanned(uuid)) return
        User.from(uuid).bans.lastOrNull()?.pardoned = true
    }

    /**
     * Unbans an IP address if it is banned.
     *
     * @param ip the IP address
     */
    @JvmStatic
    fun unbanIP(ip: String) {
        if (!isIPBanned(ip)) return
        BannedIPs.unbanIP(ip)
    }

    /**
     * Checks if a player is muted.
     * Returns true if the mute has not been pardoned and has not yet expired.
     *
     * @param uuid the player's UUID
     * @return if the player is muted
     */
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

    /**
     * Checks if a player is banned.
     * Returns true if the ban has not been pardoned and has not yet expired.
     *
     * @param uuid the player's UUID
     * @return if the player is banned
     */
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

    /**
     * Checks if an IP address is banned.
     * Returns true if the ban has not been pardoned and has not yet expired.
     *
     * @param ip the IP address
     * @return if the IP address is banned
     */
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

    /**
     * Returns a player's mutes. May be empty if the player has no mutes.
     *
     * @param uuid the player's UUID
     * @return a list of the player's mutes
     */
    @JvmStatic
    fun getMutes(uuid: UUID): List<Mute> = User.from(uuid).mutes

    /**
     * Returns a player's last mute, or null if the player has no mutes.
     *
     * @param uuid the player's UUID
     * @return the player's last mute
     */
    @JvmStatic
    fun getMute(uuid: UUID): Mute? = getMutes(uuid).lastOrNull()

    /**
     * Returns a player's bans. May be empty if the player has no bans.
     *
     * @param uuid the player's UUID
     * @return a list of the player's bans
     */
    @JvmStatic
    fun getBans(uuid: UUID): List<Ban> = User.from(uuid).bans

    /**
     * Returns a player's last ban, or null if the player has no bans.
     *
     * @param uuid the player's UUID
     * @return the player's last ban
     */
    @JvmStatic
    fun getBan(uuid: UUID): Ban? = getBans(uuid).lastOrNull()

    /**
     * Returns an IP address's bans. May be empty if the IP has no bans.
     *
     * @param ip the IP address
     * @return a list of the IP address's bans
     */
    @JvmStatic
    fun getIPBans(ip: String): List<IPBan> = BannedIPs.getIPBans(ip)

    /**
     * Returns an IP address's last ban, or null if the IP has no bans.
     *
     * @param ip the IP address
     * @return the IP address's last ban
     */
    @JvmStatic
    fun getIPBan(ip: String): IPBan? = BannedIPs.getIPBan(ip)

    /**
     * Returns an IP address's last ban based on a player that has tried to connect with this IP,
     * or null if the IP has no bans or the player was not found in a ban.
     *
     * @param uuid the player's UUID
     * @return the IP address's last ban
     */
    @JvmStatic
    fun getIPBan(uuid: UUID): IPBan? = BannedIPs.getIPBan(uuid)
}