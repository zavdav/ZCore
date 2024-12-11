package org.betamc.core.listeners

import org.betamc.core.data.SpawnData
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.time.LocalDateTime

class PlayerListener : Listener {

    @EventHandler(priority = Event.Priority.Highest)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val isFirstJoin = !PlayerMap.isPlayerKnown(event.player.uniqueId)
        val bmcPlayer = PlayerMap.getPlayer(event.player)
        val spawn = SpawnData.getSpawn(event.player.world)

        if (isFirstJoin) {
            bmcPlayer.firstJoin = LocalDateTime.now()
            if (spawn != null) event.player.teleport(spawn)
        }

        bmcPlayer.updateOnJoin(event.player.name)
        Utils.updateVanishedPlayers()
    }

    @EventHandler(priority = Event.Priority.Highest)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val bmcPlayer = PlayerMap.getPlayer(event.player)

        bmcPlayer.updateOnQuit()
        if (bmcPlayer.savedInventory != null) {
            event.player.inventory.contents = bmcPlayer.savedInventory
            bmcPlayer.savedInventory = null
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        if (PlayerMap.getPlayer(player).isGod) {
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