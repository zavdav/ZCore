package org.poseidonplugins.zcore.player

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.poseidonplugins.zcore.data.PlayerData
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

    var savedInventory: Array<ItemStack>? = null
}