package org.poseidonplugins.zcore.player

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.data.PlayerData
import org.poseidonplugins.zcore.permissions.PermissionHandler
import org.poseidonplugins.zcore.util.formatString
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

    val prefix: String
        get() = PermissionHandler.getPrefix(this)

    val suffix: String
        get() = PermissionHandler.getSuffix(this)

    var savedInventory: Array<ItemStack>? = null

    fun updateDisplayName() {
        val nickname = if (nickname == username) username
            else "${colorize(Config.getString("nickPrefix"))}$nickname"
        val displayName = formatString(colorize(Config.getString("nickFormat")),
            "prefix" to prefix, "nickname" to nickname, "suffix" to suffix)
        onlinePlayer.displayName = "§f${displayName.trim()}§f"
    }
}