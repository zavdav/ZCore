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
import org.poseidonplugins.zcore.data.Punishments
import org.poseidonplugins.zcore.data.SpawnData
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.user.UserMap
import org.poseidonplugins.zcore.util.*
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.kickBanned
import org.poseidonplugins.zcore.util.Utils.kickBannedIp
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class PlayerListener : Listener {

    @EventHandler(priority = Event.Priority.High)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player

        if (Punishments.isIPBanned(event.address.hostAddress)) {
            val ip = event.address.hostAddress
            val ipBan = Punishments.getIPBan(ip)!!
            if (player.uniqueId !in ipBan.uuids) ipBan.addUUID(player.uniqueId)
            when (ipBan.until == null) {
                true -> event.kickBannedIp("permIpBanFormat", "reason" to ipBan.reason)
                false -> event.kickBannedIp("tempIpBanFormat",
                    "datetime" to ipBan.until.truncatedTo(ChronoUnit.MINUTES),
                    "reason" to ipBan.reason)
            }
        } else if (Punishments.isBanned(player.uniqueId)) {
            val ban = Punishments.getBan(player.uniqueId)!!
            when (ban.until == null) {
                true -> event.kickBanned("permBanFormat", "reason" to ban.reason)
                false -> event.kickBanned("tempBanFormat",
                    "datetime" to ban.until.truncatedTo(ChronoUnit.MINUTES),
                    "reason" to ban.reason)
            }
        }
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val isFirstJoin = !UserMap.isUserKnown(event.player.uniqueId)
        val user = User.from(event.player)
        val spawn = SpawnData.getSpawn(event.player.world)

        if (isFirstJoin) {
            user.firstJoin = LocalDateTime.now()
            if (spawn != null) event.player.teleport(spawn)
        }

        user.updateOnJoin(event.player.name)
        user.updateDisplayName()
        Utils.updateVanishedPlayers()

        if (!Config.isEmpty("motd") && hasPermission(event.player, "zcore.motd")) {
            event.player.performCommand("motd")
        }
        if (user.mails.isNotEmpty()) event.player.sendTl("newMail")

        event.joinMessage = formatProperty("joinMsgFormat", "player" to event.player.name)
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val user = User.from(event.player)

        if (user.isAfk) user.isAfk = false
        if (user.savedInventory != null) {
            event.player.inventory.contents = user.savedInventory
            user.savedInventory = null
        }
        user.cachedPlayTime = user.playTime
        event.quitMessage = formatProperty("leaveMsgFormat", "player" to event.player.name)
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Low)
    fun onPlayerKick(event: PlayerKickEvent) {
        val isBanned = Punishments.isBanned(event.player.uniqueId)
                    || Punishments.isIPBanned(event.player.address.address.hostAddress)
        event.leaveMessage = formatProperty(if (isBanned) "banMsgFormat" else "kickMsgFormat",
            "player" to event.player.name)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerChat(event: PlayerChatEvent) {
        val user = User.from(event.player)
        if (user.checkIsMuted()) {
            event.isCancelled = true
            return
        }
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
            val targetUser = User.from(it)
            !targetUser.seesChat || event.player.uniqueId in targetUser.ignores &&
            !hasPermission(event.player, "zcore.ignore.exempt")
        }

        user.updateActivity()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        if (!event.message.equals("/afk", true)) {
            User.from(event.player).updateActivity()
        }
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.High)
    fun onPlayerDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player
        val user = User.from(player)
        if (user.isGod || Config.getBoolean("protectAfkPlayers") && user.isAfk) {
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

        val user = User.from(event.player)
        if (!user.isAfk) {
            user.updateActivity()
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
            user.updateActivity()
        }
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = SpawnData.getSpawn(event.respawnLocation.world) ?: return
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerBreakBlock(event: BlockBreakEvent) {
        User.from(event.player).updateActivity()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerPlaceBlock(event: BlockPlaceEvent) {
        User.from(event.player).updateActivity()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerEmptyBucket(event: PlayerBucketEmptyEvent) {
        User.from(event.player).updateActivity()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerFillBucket(event: PlayerBucketFillEvent) {
        User.from(event.player).updateActivity()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val user = User.from(event.player)
        if (user.isAfk || user.vanished) event.isCancelled = true
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        val user = User.from(event.player)
        if (user.isAfk || user.vanished) event.isCancelled = true
    }
}