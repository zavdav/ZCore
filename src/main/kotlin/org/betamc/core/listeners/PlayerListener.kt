package org.betamc.core.listeners

import org.betamc.core.config.Property
import org.betamc.core.data.BanData
import org.betamc.core.data.SpawnData
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
import org.betamc.core.util.Utils.safeSubstring
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class PlayerListener : Listener {

    @EventHandler(priority = Event.Priority.Highest)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val bmcPlayer = PlayerMap.getPlayer(event.player)

        if (BanData.isIPBanned(event.address.hostAddress)) {
            val ip = event.address.hostAddress
            val ipBan = BanData.getIPBan(ip)!!
            when (ipBan.until == null) {
                true -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    Utils.formatColorize(Property.IPBAN_PERMANENT, ipBan.reason).safeSubstring(0, 99))
                false -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    Utils.formatColorize(Property.IPBAN_TEMPORARY,
                    ipBan.until.truncatedTo(ChronoUnit.MINUTES), ipBan.reason).safeSubstring(0, 99))
            }
        } else if (bmcPlayer.isBanned) {
            val ban = BanData.getBan(event.player.uniqueId)!!
            when (ban.until == null) {
                true -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    Utils.formatColorize(Property.BAN_PERMANENT, ban.reason).safeSubstring(0, 99))
                false -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    Utils.formatColorize(Property.BAN_TEMPORARY,
                    ban.until.truncatedTo(ChronoUnit.MINUTES), ban.reason).safeSubstring(0, 99))
            }
        }
    }

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