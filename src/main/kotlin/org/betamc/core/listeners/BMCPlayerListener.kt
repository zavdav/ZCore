package org.betamc.core.listeners

import org.betamc.core.player.PlayerMap
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent

class BMCPlayerListener : Listener {

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val bmcPlayer = PlayerMap.getPlayer(event.player)
        bmcPlayer.updateOnJoin(event.player.name)
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Highest)
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        if (PlayerMap.getPlayer(player).getGodStatus()) {
            player.fireTicks = 0
            player.remainingAir = player.maximumAir
            event.isCancelled = true
        }
    }
}