package me.zavdav.zcore.listeners

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.Spawnpoints
import me.zavdav.zcore.data.UUIDCache
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.*
import org.bukkit.block.ContainerBlock
import org.bukkit.entity.Player
import org.bukkit.entity.StorageMinecart
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.hasPermission

class PlayerListener : Listener {

    @EventHandler(priority = Event.Priority.High)
    fun onPlayerLogin(event: PlayerLoginEvent) {
        val player = event.player
        UUIDCache.addEntry(player.uniqueId, player.name)

        val user = User.from(event.player)
        if (user.checkIsIPBanned(event)) return
        user.checkIsBanned(event)
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val user = User.from(event.player)
        val isFirstJoin = user.firstJoin == -1L

        if (isFirstJoin) {
            user.firstJoin = System.currentTimeMillis()
            broadcast(Config.firstJoinMessage, event.player)
            val spawn = Spawnpoints.getSpawn(event.player.world)
            if (spawn != null) event.player.teleport(spawn)
        }
        user.updateOnJoin(event.player.name)

        if (!hasPermission(event.player, "zcore.god")) user.isGod = false
        if (!hasPermission(event.player, "zcore.vanish")) user.isVanished = false
        if (!hasPermission(event.player, "zcore.socialspy")) user.socialSpy = false
        if (!hasPermission(event.player, "zcore.togglechat")) user.seesChat = true
        if (!hasPermission(event.player, "zcore.nick")) user.nickname = null

        user.updateDisplayName()
        Utils.updateVanishedPlayers()

        if (Config.motd.isNotEmpty() && hasPermission(event.player, "zcore.motd")) {
            event.player.performCommand("motd")
        }
        if (user.mails.isNotEmpty() && hasPermission(event.player, "zcore.mail")) {
            event.player.sendTl("newMail")
        }

        event.joinMessage = format(Config.joinMsg, event.player)
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val user = User.from(event.player)

        if (user.isAfk) user.isAfk = false
        if (user.savedInventory != null) {
            event.player.inventory.contents = user.savedInventory
            user.savedInventory = null
        }
        user.isInvSee = false

        user.updatePlayTime()
        user.cachedPlayTime = user.playTime
        user.banExempt = hasPermission(event.player, "zcore.ban.exempt")
        user.muteExempt = hasPermission(event.player, "zcore.mute.exempt")

        event.quitMessage = format(Config.leaveMsg, event.player)
    }

    @EventHandler(ignoreCancelled = true, priority = Event.Priority.Low)
    fun onPlayerKick(event: PlayerKickEvent) {
        val isBanned = Punishments.isPlayerBanned(event.player.uniqueId)
                    || Punishments.isIPBanned(event.player.address.address.hostAddress)
        event.leaveMessage = format(if (isBanned) Config.banMsg else Config.kickMsg, event.player)
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
        event.format = format(Config.chat, "displayname" to "%1\$s", "message" to "%2\$s")

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

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        if (event.clickedBlock.state is ContainerBlock && User.from(event.player).isInvSee) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        if (event.rightClicked is StorageMinecart && User.from(event.player).isInvSee) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerBreakBlock(event: BlockBreakEvent) {
        User.from(event.player).updateActivity()
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerPlaceBlock(event: BlockPlaceEvent) {
        val user = User.from(event.player)
        if (user.isInvSee) event.isCancelled = true
        user.updateActivity()
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
        if (user.isAfk || user.isVanished || user.isInvSee) {
            event.isCancelled = true
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        val user = User.from(event.player)
        if (user.isAfk || user.isVanished || user.isInvSee) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = Event.Priority.Low)
    fun onPlayerRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = Spawnpoints.getSpawn(event.respawnLocation.world) ?: return
    }
}