package org.poseidonplugins.zcore.listeners

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.config.Property
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.data.SpawnData
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.safeSubstring
import org.poseidonplugins.zcore.util.format
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class PlayerListener : Listener {

    @EventHandler(priority = Event.Priority.High)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player

        if (BanData.isIPBanned(event.address.hostAddress)) {
            val ip = event.address.hostAddress
            val ipBan = BanData.getIPBan(ip)!!
            if (!ipBan.uuids.contains(player.uniqueId)) ipBan.addUUID(player.uniqueId)
            when (ipBan.until == null) {
                true -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    format(Property.IPBAN_PERMANENT, "reason" to ipBan.reason).safeSubstring(0, 99))
                false -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    format(Property.IPBAN_TEMPORARY,
                        "datetime" to ipBan.until.truncatedTo(ChronoUnit.MINUTES),
                        "reason" to ipBan.reason).safeSubstring(0, 99))
            }
        } else if (BanData.isBanned(player.uniqueId)) {
            val ban = BanData.getBan(player.uniqueId)!!
            when (ban.until == null) {
                true -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    format(Property.BAN_PERMANENT, "reason" to ban.reason).safeSubstring(0, 99))
                false -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    format(Property.BAN_TEMPORARY,
                        "datetime" to ban.until.truncatedTo(ChronoUnit.MINUTES),
                        "reason" to ban.reason).safeSubstring(0, 99))
            }
        }
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val isFirstJoin = !PlayerMap.isPlayerKnown(event.player.uniqueId)
        val zPlayer = PlayerMap.getPlayer(event.player)
        val spawn = SpawnData.getSpawn(event.player.world)

        if (isFirstJoin) {
            zPlayer.firstJoin = LocalDateTime.now()
            if (spawn != null) event.player.teleport(spawn)
        }

        zPlayer.updateOnJoin(event.player.name)
        Utils.updateVanishedPlayers()

        if (Property.MOTD.toString().isNotEmpty() &&
            hasPermission(event.player, "zcore.motd")) {
            event.player.performCommand("motd")
        }
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val zPlayer = PlayerMap.getPlayer(event.player)

        zPlayer.updateOnQuit()
        if (zPlayer.savedInventory != null) {
            event.player.inventory.contents = zPlayer.savedInventory
            zPlayer.savedInventory = null
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.High)
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        if (PlayerMap.getPlayer(player).isGod) {
            player.fireTicks = 0
            player.remainingAir = player.maximumAir
            event.isCancelled = true
        }
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = SpawnData.getSpawn(event.respawnLocation.world) ?: return
    }
}