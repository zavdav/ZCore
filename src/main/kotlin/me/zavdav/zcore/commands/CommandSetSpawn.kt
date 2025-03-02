package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.data.Spawnpoints
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetSpawn : AbstractCommand(
    "setspawn",
    "Sets the world spawn to your current location.",
    "/setspawn [reset]",
    "zcore.setspawn",
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val loc = player.location
        if (args.size == 1 && args[0].equals("reset", true)) {
            Spawnpoints.removeSpawn(loc.world.name)
            player.sendTl("resetSpawn", "world" to loc.world.name)

        } else {
            Spawnpoints.setSpawn(loc.world.name, loc)
            sender.sendTl("setSpawn",
                "world" to loc.world.name,
                "coordinates" to "${loc.blockX}, ${loc.blockY}, ${loc.blockZ}")
        }
    }
}