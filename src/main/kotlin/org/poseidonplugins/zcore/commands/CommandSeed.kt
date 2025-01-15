package org.poseidonplugins.zcore.commands

import org.bukkit.Bukkit
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.util.sendTl

class CommandSeed : Command(
    "seed",
    description = "Shows the world's seed.",
    usage = "/seed",
    permission = "zcore.seed",
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        event.sender.sendTl("worldSeed", "seed" to Bukkit.getWorlds()[0].seed)
    }
}