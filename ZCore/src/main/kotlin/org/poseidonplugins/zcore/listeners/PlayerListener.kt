package org.poseidonplugins.zcore.listeners

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.SignChangeEvent
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
                true -> event.kickBannedIp("permaIpBanned", "reason" to ipBan.reason)
                false -> event.kickBannedIp("tempIpBanned",
                    "datetime" to ipBan.until.truncatedTo(ChronoUnit.MINUTES),
                    "reason" to ipBan.reason)
            }
        } else if (Punishments.isBanned(player.uniqueId)) {
            val ban = Punishments.getBan(player.uniqueId)!!
            when (ban.until == null) {
                true -> event.kickBanned("permaBanned", "reason" to ban.reason)
                false -> event.kickBanned("tempBanned",
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

        if (Config.motd.isNotEmpty() && hasPermission(event.player, "zcore.motd")) {
            event.player.performCommand("motd")
        }
        if (user.mails.isNotEmpty()) event.player.sendTl("newMail")

        event.joinMessage = formatString(Config.joinMsgFormat, event.player)
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val user = User.from(event.player)

        if (user.isAfk) user.isAfk = false
        if (user.savedInventory != null) {
            event.player.inventory.contents = user.savedInventory
            user.savedInventory = null
        }
        user.updatePlayTime()
        user.cachedPlayTime = user.playTime
        event.quitMessage = formatString(Config.leaveMsgFormat, event.player)
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Low)
    fun onPlayerKick(event: PlayerKickEvent) {
        val isBanned = Punishments.isBanned(event.player.uniqueId)
                    || Punishments.isIPBanned(event.player.address.address.hostAddress)
        event.leaveMessage = formatString(if (isBanned) Config.banMsgFormat else Config.kickMsgFormat, event.player)
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
        event.format = formatString(Config.chatFormat, "displayname" to "%1\$s", "message" to "%2\$s")

        val radius = Config.chatRadius
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
        if (user.isGod || Config.protectAfkPlayers && user.isAfk) {
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

        if (Config.protectAfkPlayers && hasPermission(event.player, "zcore.afk")) {
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
    fun onPlayerChangeSign(event: SignChangeEvent) {
        if (hasPermission(event.player, "zcore.signs.color")) {
            for (i in event.lines.indices) {
                event.setLine(i, colorize(event.getLine(i)))
            }
        }
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