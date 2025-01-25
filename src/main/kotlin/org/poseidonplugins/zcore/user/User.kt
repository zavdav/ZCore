package org.poseidonplugins.zcore.user

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.data.Punishments
import org.poseidonplugins.zcore.data.UserData
import org.poseidonplugins.zcore.hooks.permissions.PermissionHandler
import org.poseidonplugins.zcore.util.broadcastTl
import org.poseidonplugins.zcore.util.formatProperty
import org.poseidonplugins.zcore.util.kick
import org.poseidonplugins.zcore.util.sendTl
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

class User private constructor(uuid: UUID) : UserData(uuid) {

    companion object {
        fun from(player: Player, register: Boolean = true): User =
            from(player.uniqueId, register)

        fun from(uuid: UUID, register: Boolean = true): User {
            if (!UserMap.isUserKnown(uuid)) UserMap.knownUsers.add(uuid)
            if (uuid in UserMap.userMap.keys) return UserMap.userMap[uuid]!!

            val user = User(uuid)
            if (register) UserMap.userMap[uuid] = user
            return user
        }
    }

    val name: String
        get() = when (isOnline) {
            true -> player.name
            false -> username
        }

    val isOnline: Boolean
        get() = uuid in Bukkit.getOnlinePlayers().map { player -> player.uniqueId }

    val player: Player
        get() = Bukkit.getOnlinePlayers().first { player -> player.uniqueId == uuid }

    val prefix: String
        get() = PermissionHandler.getPrefix(this)

    val suffix: String
        get() = PermissionHandler.getSuffix(this)

    var isAfk: Boolean = false

    var replyTo: Player? = null

    var savedInventory: Array<ItemStack>? = null

    var cachedPlayTime: Long = playTime

    fun getDisplayName(useNick: Boolean): String {
        val nickname = if (!useNick || nickname == username) {
            username
        } else {
            "${Config.getString("nickPrefix")}${nickname}"
        }
        val displayName = formatProperty("nickFormat",
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
        lastSeen = LocalDateTime.now()
    }

    fun checkIsAfk() {
        if (!isOnline) return

        if (!isAfk && Duration.between(lastSeen, LocalDateTime.now()).seconds >= Config.getInt("afkTime")) {
            setInactive()
        }
        if (isAfk && Duration.between(lastSeen, LocalDateTime.now()).seconds >= Config.getInt("afkKickTime")) {
            player.kick("afkKickReason")
        }
    }

    fun checkIsMuted(): Boolean {
        if (!isOnline) return false

        if (Punishments.isMuted(uuid)) {
            val mute = Punishments.getMute(uuid) ?: return false
            when (mute.until == null) {
                true -> player.sendTl("permMute", "reason" to mute.reason)
                false -> player.sendTl("tempMute",
                    "datetime" to mute.until.truncatedTo(ChronoUnit.MINUTES),
                    "reason" to mute.reason
                )
            }
            return true
        }
        return false
    }
}