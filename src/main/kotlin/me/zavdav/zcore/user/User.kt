package me.zavdav.zcore.user

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.BannedIPs
import me.zavdav.zcore.data.UserData
import me.zavdav.zcore.hooks.permissions.PermissionHandler
import me.zavdav.zcore.util.broadcastTl
import me.zavdav.zcore.util.format
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.kick
import me.zavdav.zcore.util.kickBanned
import me.zavdav.zcore.util.kickBannedIP
import me.zavdav.zcore.util.sendTl
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.inventory.ItemStack
import java.util.UUID

class User private constructor(uuid: UUID) : UserData(uuid) {

    companion object {
        fun from(player: Player): User =
            from(player.uniqueId)

        fun from(uuid: UUID): User {
            if (!UserMap.isUserKnown(uuid)) UserMap.knownUsers.add(uuid)
            if (uuid in UserMap.userMap.keys) return UserMap.userMap[uuid]!!

            val user = User(uuid)
            UserMap.userMap[uuid] = user
            return user
        }
    }

    enum class TeleportType {
        TPA, TPAHERE
    }

    val name: String
        get() = when (isOnline) {
            true -> player.name
            false -> username
        }

    val isOnline: Boolean
        get() = uuid in Bukkit.getOnlinePlayers().map { it.uniqueId }

    val player: Player
        get() = Bukkit.getOnlinePlayers().first { it.uniqueId == uuid }

    val prefix: String
        get() = PermissionHandler.getPrefix(this)

    val suffix: String
        get() = PermissionHandler.getSuffix(this)

    var isAfk: Boolean = false

    var replyTo: Player? = null

    var tpRequest: Pair<Player, TeleportType>? = null

    var isInvSee: Boolean = false

    var savedInventory: Array<ItemStack>? = null

    var cachedPlayTime: Long = playTime

    fun getNick(): String {
        val sb = StringBuilder()
        if (player.isOp) sb.append(Config.operatorColor)
        if (nickname != null) {
            sb.append(Config.nickPrefix + nickname)
        } else {
            sb.append(username)
        }
        return sb.toString()
    }

    fun getDisplayName(useNick: Boolean): String {
        val nickname = if (useNick) getNick() else username
        val displayName = format(Config.displayNameFormat,
            "prefix" to prefix, "nickname" to nickname, "suffix" to suffix)
        return "§f${displayName.trim()}§f"
    }

    fun updateDisplayName() {
        player.displayName = getDisplayName(true)
    }

    fun setInactive() {
        if (!isOnline) return
        isAfk = true
        broadcastTl("nowAfk", player)
    }

    fun updateActivity() {
        if (!isOnline) return
        if (isAfk) {
            isAfk = false
            broadcastTl("noLongerAfk", player)
        }
        updateDisplayName()
        lastSeen = System.currentTimeMillis()
    }

    fun updatePlayTime() {
        playTime = cachedPlayTime + System.currentTimeMillis() - lastJoin
    }

    fun checkKitCooldowns() {
        val kitCooldowns = kitCooldowns.toMutableMap()
        kitCooldowns.entries.removeIf { System.currentTimeMillis() > it.value }
        this.kitCooldowns = kitCooldowns
    }

    fun checkIsAfk() {
        if (!isOnline) return

        if (!isAfk && System.currentTimeMillis() - lastSeen >= Config.afkTime * 1000) {
            setInactive()
        }
        if (isAfk && System.currentTimeMillis() - lastSeen >= Config.afkKickTime * 1000 &&
            !player.isAuthorized("zcore.afk.kick.exempt"))
        {
            player.kick("afkKickReason")
        }
    }

    fun checkIsMuted(): Boolean {
        if (!isOnline) return false

        if (Punishments.isPlayerMuted(uuid)) {
            val mute = Punishments.getMute(uuid) ?: return false
            when (mute.duration) {
                null -> player.sendTl("muteScreen", "reason" to mute.reason)
                else -> player.sendTl("tempMuteScreen",
                    "duration" to formatDuration(
                        mute.timeIssued + mute.duration * 1000 - System.currentTimeMillis()
                    ),
                    "reason" to mute.reason
                )
            }
            return true
        }
        return false
    }

    fun checkIsBanned(): Boolean {
        if (!isOnline) return false

        if (Punishments.isPlayerBanned(uuid)) {
            val ban = Punishments.getBan(uuid) ?: return false
            when (ban.duration) {
                null -> player.kick("banScreen", "reason" to ban.reason)
                else -> player.kick("tempBanScreen",
                    "duration" to formatDuration(
                        ban.timeIssued + ban.duration * 1000 - System.currentTimeMillis()
                    ),
                    "reason" to ban.reason)
            }
            return true
        }
        return false
    }

    fun checkIsBanned(event: PlayerLoginEvent): Boolean {
        if (Punishments.isPlayerBanned(event.player.uniqueId)) {
            val ban = Punishments.getBan(event.player.uniqueId) ?: return false
            when (ban.duration) {
                null -> event.kickBanned("banScreen", "reason" to ban.reason)
                else -> event.kickBanned("tempBanScreen",
                    "duration" to formatDuration(
                        ban.timeIssued + ban.duration * 1000 - System.currentTimeMillis()
                    ),
                    "reason" to ban.reason)
            }
            return true
        }
        return false
    }

    fun checkIsIPBanned(): Boolean {
        if (!isOnline) return false

        val ip = player.address.address.hostAddress
        if (Punishments.isIPBanned(ip)) {
            val ipBan = Punishments.getIPBan(ip)!!
            if (uuid !in ipBan.uuids) BannedIPs.addUUID(uuid, ip)

            when (ipBan.duration) {
                null -> player.kick("ipBanScreen", "reason" to ipBan.reason)
                else -> player.kick("tempIpBanScreen",
                    "duration" to formatDuration(
                        ipBan.timeIssued + ipBan.duration * 1000 - System.currentTimeMillis()
                    ),
                    "reason" to ipBan.reason)
            }
            return true
        }
        return false
    }

    fun checkIsIPBanned(event: PlayerLoginEvent): Boolean {
        if (Punishments.isIPBanned(event.address.hostAddress)) {
            val ip = event.address.hostAddress
            val ipBan = Punishments.getIPBan(ip) ?: return false
            if (uuid !in ipBan.uuids) BannedIPs.addUUID(uuid, ip)
            when (ipBan.duration) {
                null -> event.kickBannedIP("ipBanScreen", "reason" to ipBan.reason)
                else -> event.kickBannedIP("tempIpBanScreen",
                    "duration" to formatDuration(
                        ipBan.timeIssued + ipBan.duration * 1000 - System.currentTimeMillis()
                    ),
                    "reason" to ipBan.reason)
            }
            return true
        }
        return false
    }
}