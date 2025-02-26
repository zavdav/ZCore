package me.zavdav.zcore.commands

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.Spawnpoints
import me.zavdav.zcore.util.Delay
import me.zavdav.zcore.util.NoFundsException
import me.zavdav.zcore.util.getSafeHeight
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandSpawn : ZCoreCommand(
    "spawn",
    description = "Teleports you to the server spawn.",
    usage = "/spawn",
    permission = "zcore.spawn",
    isPlayerOnly = true,
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var loc = player.world.spawnLocation
        loc.x = loc.blockX + 0.5
        loc.z = loc.blockZ + 0.5
        loc = Spawnpoints.getSpawn(player.world) ?: loc
        loc.y = getSafeHeight(loc).toDouble()

        val delay = Config.teleportDelay
        if (delay > 0) {
            event.sender.sendTl("commencingTeleport", "location" to loc.world.name, "delay" to delay)
            event.sender.sendTl("doNotMove")
        }
        Delay(player, delay) {
            try {
                charge(player)
                player.teleport(loc)
                player.sendTl("teleportedToSpawn", "world" to loc.world.name)
            } catch (e: NoFundsException) {
                for (message in e.messages) {
                    player.sendMessage(message)
                }
            }
        }
    }
}