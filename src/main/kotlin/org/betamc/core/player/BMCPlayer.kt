package org.betamc.core.player

import org.betamc.core.data.PlayerData
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID

class BMCPlayer(private val uuid: UUID) : PlayerData(uuid) {

    var savedInventory: Array<ItemStack>? = null

    fun isOnline(): Boolean = Bukkit.getOnlinePlayers().map { player -> player.uniqueId }.contains(uuid)

    fun getName(): String =
        when (isOnline()) {
            true -> getOnlinePlayer().name
            else -> getUsernameJSON()
        }

    fun getOnlinePlayer(): Player = Bukkit.getOnlinePlayers().filter { player -> player.uniqueId == uuid }[0]

    fun getUUID(): UUID = uuid
}