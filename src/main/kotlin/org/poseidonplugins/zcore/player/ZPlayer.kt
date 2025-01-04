package org.poseidonplugins.zcore.player

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.data.PlayerData
import org.poseidonplugins.zcore.permissions.PermissionHandler
import org.poseidonplugins.zcore.util.Utils.safeSubstring
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatProperty
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

class ZPlayer(uuid: UUID) : PlayerData(uuid) {

    val name: String
        get() = when (isOnline) {
            true -> onlinePlayer.name
            false -> username
        }

    val isOnline: Boolean
        get() = Bukkit.getOnlinePlayers().map { player -> player.uniqueId }.contains(uuid)

    val onlinePlayer: Player
        get() = Bukkit.getOnlinePlayers().first { player -> player.uniqueId == uuid }

    val displayName: String
        get() {
            val nickname = if (nickname == username) username
                else "${colorize(Config.getString("nickPrefix"))}$nickname"
            val displayName = formatProperty("nickFormat",
                "prefix" to prefix, "nickname" to nickname, "suffix" to suffix)
            return "§f${displayName.trim()}§f"
        }

    val prefix: String
        get() = PermissionHandler.getPrefix(this)

    val suffix: String
        get() = PermissionHandler.getSuffix(this)

    var isAFK: Boolean = false
        set(value) {
            field = value
            if (!value) updateActivity()
            if (isOnline) {
                Bukkit.broadcastMessage(format(if (value) "nowAfk" else "noLongerAfk", onlinePlayer))
            }
        }

    var savedInventory: Array<ItemStack>? = null

    fun updateDisplayName() {
        onlinePlayer.displayName = displayName
    }

    fun updateActivity() {
        if (!isOnline) return
        lastSeen = LocalDateTime.now()
    }

    fun checkIsAfk() {
        if (!isOnline) {
            if (!isAFK) isAFK = true
            return
        }

        if (!isAFK && Duration.between(lastSeen, LocalDateTime.now()).seconds >= Config.getInt("afkTime")) {
            isAFK = true
        }
        if (isAFK && Duration.between(lastSeen, LocalDateTime.now()).seconds >= Config.getInt("afkKickTime")) {
            onlinePlayer.kickPlayer(formatProperty("afkKickReason").safeSubstring(0, 99))
        }
    }
}