package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.sendTl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class CommandSeed : AbstractCommand(
    "seed",
    "Shows the world's seed.",
    "/seed",
    "zcore.seed",
    false,
    maxArgs = 0
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        sender.sendTl("worldSeed", "seed" to Bukkit.getWorlds()[0].seed)
    }
}