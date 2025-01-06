package org.poseidonplugins.zcore.listeners

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.data.SpawnData
import org.poseidonplugins.zcore.exceptions.UnsafeDestinationException
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.safeSubstring
import org.poseidonplugins.zcore.util.formatProperty
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
                    formatProperty("permIpBanFormat",
                        "reason" to ipBan.reason).safeSubstring(0, 99))
                false -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    formatProperty("tempIpBanFormat",
                        "datetime" to ipBan.until.truncatedTo(ChronoUnit.MINUTES),
                        "reason" to ipBan.reason).safeSubstring(0, 99))
            }
        } else if (BanData.isBanned(player.uniqueId)) {
            val ban = BanData.getBan(player.uniqueId)!!
            when (ban.until == null) {
                true -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    formatProperty("permBanFormat", "reason" to ban.reason).safeSubstring(0, 99))
                false -> event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    formatProperty("tempBanFormat",
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
        zPlayer.updateDisplayName()
        Utils.updateVanishedPlayers()

        if (!Config.isEmpty("motd") && hasPermission(event.player, "zcore.motd")) {
            event.player.performCommand("motd")
        }
        event.joinMessage = formatProperty("joinMsgFormat", "player" to event.player.name)
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val zPlayer = PlayerMap.getPlayer(event.player)

        if (zPlayer.isAfk) zPlayer.isAfk = false
        if (zPlayer.savedInventory != null) {
            event.player.inventory.contents = zPlayer.savedInventory
            zPlayer.savedInventory = null
        }
        event.quitMessage = formatProperty("leaveMsgFormat", "player" to event.player.name)
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Low)
    fun onPlayerKick(event: PlayerKickEvent) {
        val isBanned = BanData.isBanned(event.player.uniqueId)
                    || BanData.isIPBanned(event.player.address.address.hostAddress)
        event.leaveMessage = formatProperty(if (isBanned) "banMsgFormat" else "kickMsgFormat",
            "player" to event.player.name)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerChat(event: PlayerChatEvent) {
        if (hasPermission(event.player, "zcore.chat.color")) {
            event.message = colorize(event.message)
        }
        event.format = formatProperty("chatFormat", "displayname" to "%1\$s", "message" to "%2\$s")

        val radius = Config.getInt("chatRadius", 0)
        if (radius != 0) {
            event.recipients.removeIf { player ->
                player.world != event.player.world || event.player.location.distance(player.location) > radius
            }
        }
        event.recipients.removeIf {
            event.player.uniqueId in PlayerMap.getPlayer(it).ignores
            && !hasPermission(event.player, "zcore.ignore.exempt")
        }

        PlayerMap.getPlayer(event.player).updateActivity()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        if (!event.message.equals("/afk", true)) {
            PlayerMap.getPlayer(event.player).updateActivity()
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.High)
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        val zPlayer = PlayerMap.getPlayer(player)
        if (zPlayer.isGod || Config.getBoolean("protectAfkPlayers") && zPlayer.isAfk) {
            player.fireTicks = 0
            player.remainingAir = player.maximumAir
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Low)
    fun onPlayerMove(event: PlayerMoveEvent) {
        val from = event.from
        val to = event.to
        val moved =
            from.blockX != to.blockX || from.blockY != to.blockY || from.blockZ != to.blockZ
        if (!moved) return

        val zPlayer = PlayerMap.getPlayer(event.player)
        if (!zPlayer.isAfk) {
            zPlayer.updateActivity()
            return
        }

        if (Config.getBoolean("protectAfkPlayers") && hasPermission(event.player, "zcore.afk")) {
            from.pitch = to.pitch
            from.yaw = to.yaw
            if (from.y > to.y) from.y = to.y
            try {
                from.y = Utils.getSafeHeight(from).toDouble()
            } catch (_: UnsafeDestinationException) {}

            event.to = from
        } else {
            zPlayer.updateActivity()
        }
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = SpawnData.getSpawn(event.respawnLocation.world) ?: return
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerBreakBlock(event: BlockBreakEvent) {
        PlayerMap.getPlayer(event.player).updateActivity()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerPlaceBlock(event: BlockPlaceEvent) {
        PlayerMap.getPlayer(event.player).updateActivity()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerEmptyBucket(event: PlayerBucketEmptyEvent) {
        PlayerMap.getPlayer(event.player).updateActivity()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerFillBucket(event: PlayerBucketFillEvent) {
        PlayerMap.getPlayer(event.player).updateActivity()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val zPlayer = PlayerMap.getPlayer(event.player)
        if (zPlayer.isAfk || zPlayer.vanished) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        val zPlayer = PlayerMap.getPlayer(event.player)
        if (zPlayer.isAfk || zPlayer.vanished) event.isCancelled = true
    }
}