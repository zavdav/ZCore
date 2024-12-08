package org.betamc.core.listeners

import org.betamc.core.data.SpawnData
import org.betamc.core.player.PlayerMap
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent

class PlayerListener : Listener {

    @EventHandler(priority = Event.Priority.Highest)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val isFirstJoin = !PlayerMap.isPlayerKnown(event.player.uniqueId)
        val bmcPlayer = PlayerMap.getPlayer(event.player)
        bmcPlayer.updateOnJoin(event.player.name)

        val spawn = SpawnData.getSpawn(event.player.world)
        if (isFirstJoin && spawn != null) {
            event.player.teleport(spawn)
        }
    }

    @EventHandler(priority = Event.Priority.Highest)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        PlayerMap.getPlayer(event.player).updateOnQuit()
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        if (PlayerMap.getPlayer(player).hasGodMode()) {
            player.fireTicks = 0
            player.remainingAir = player.maximumAir
            event.isCancelled = true
        }
    }

    @EventHandler(priority = Event.Priority.Highest)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = SpawnData.getSpawn(event.respawnLocation.world) ?: return
    }
}