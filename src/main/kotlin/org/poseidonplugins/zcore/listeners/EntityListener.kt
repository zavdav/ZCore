package org.poseidonplugins.zcore.listeners

import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.poseidonplugins.zcore.player.PlayerMap

class EntityListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        when (event.cause) {
            EntityDamageEvent.DamageCause.ENTITY_ATTACK -> {
                val dmgEvent = event as EntityDamageByEntityEvent
                if (dmgEvent.damager == null || dmgEvent.damager !is Player) return
                if (PlayerMap.getPlayer(dmgEvent.damager as Player).isAFK) {
                    event.isCancelled = true
                }
            }
            EntityDamageEvent.DamageCause.PROJECTILE -> {
                val dmgEvent = event as EntityDamageByEntityEvent
                val shooter = (dmgEvent.damager as Projectile).shooter
                if (shooter == null || shooter !is Player) return
                if (PlayerMap.getPlayer(shooter).isAFK) {
                    event.isCancelled = true
                }
            }
            else -> {}
        }
    }
}