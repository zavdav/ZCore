package me.zavdav.zcore.listeners

import me.zavdav.zcore.user.User
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityTargetEvent

class EntityListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event !is EntityDamageByEntityEvent) return

        when (event.cause) {
            EntityDamageEvent.DamageCause.ENTITY_ATTACK -> {
                if (event.damager == null || event.damager !is Player) return
                if (User.from(event.damager as Player).isAfk) {
                    event.isCancelled = true
                }
            }
            EntityDamageEvent.DamageCause.PROJECTILE -> {
                val shooter = (event.damager as Projectile).shooter
                if (shooter == null || shooter !is Player) return
                if (User.from(shooter).isAfk) {
                    event.isCancelled = true
                }
            }
            else -> {}
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onEntityTarget(event: EntityTargetEvent) {
        if (event.target !is Player) return
        val user = User.from(event.target as Player)
        if (user.isAfk || user.vanished) event.isCancelled = true
    }
}