package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.Spawnpoints
import me.zavdav.zcore.util.Delay
import me.zavdav.zcore.util.NoFundsException
import me.zavdav.zcore.util.getSafeHeight
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSpawn : AbstractCommand(
    "spawn",
    "Teleports you to the server spawn.",
    "/spawn",
    "zcore.spawn",
    maxArgs = 0
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var loc = player.world.spawnLocation
        loc.x = loc.blockX + 0.5
        loc.z = loc.blockZ + 0.5
        loc = Spawnpoints.getSpawn(player.world) ?: loc
        loc.y = getSafeHeight(loc).toDouble()

        val delay = Config.teleportDelay
        if (delay > 0) {
            sender.sendTl("commencingTeleport", "location" to loc.world.name, "delay" to delay)
            sender.sendTl("doNotMove")
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