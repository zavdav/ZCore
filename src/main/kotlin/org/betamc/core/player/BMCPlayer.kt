package org.betamc.core.player

import org.betamc.core.data.PlayerData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID

class BMCPlayer(uuid: UUID) : PlayerData(uuid) {

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