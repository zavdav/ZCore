package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.Spawnpoints
import me.zavdav.zcore.util.Delay
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
            sender.sendTl("commencingTeleport", loc.world.name, delay)
            sender.sendTl("doNotMove")
        }
        Delay(sender, player, delay) {
            charge(player)
            player.teleport(loc)
            player.sendTl("teleportedToSpawn", loc.world.name)
        }
    }
}