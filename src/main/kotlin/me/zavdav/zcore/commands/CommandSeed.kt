package me.zavdav.zcore.commands

import me.zavdav.zcore.util.sendTl
import org.bukkit.Bukkit
import org.poseidonplugins.commandapi.CommandEvent

class CommandSeed : ZCoreCommand(
    "seed",
    description = "Shows the world's seed.",
    usage = "/seed",
    permission = "zcore.seed",
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        event.sender.sendTl("worldSeed", "seed" to Bukkit.getWorlds()[0].seed)
    }
}