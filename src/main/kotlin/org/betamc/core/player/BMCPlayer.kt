package org.betamc.core.player

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

class BMCPlayer(private val uuid: UUID) : PlayerData(uuid) {

    fun isOnline(): Boolean = Bukkit.getOnlinePlayers().map { player -> player.uniqueId }.contains(uuid)

    fun getName(): String =
        when (isOnline()) {
            true -> getOnlinePlayer().name
            else -> getUsernameJSON()
        }

    fun getOnlinePlayer(): Player = Bukkit.getOnlinePlayers().filter { player -> player.uniqueId == uuid }[0]

    fun getUUID(): UUID = uuid
}