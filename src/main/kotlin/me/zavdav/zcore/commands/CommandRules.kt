package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.send
import org.bukkit.command.CommandSender

class CommandRules : AbstractCommand(
    "rules",
    "Shows the server's rules.",
    "/rules",
    "zcore.rules",
    false,
    maxArgs = 0
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        assert(Config.rules.isNotEmpty(), "noRules")
        Config.rules.forEach { sender.send(it) }
    }
}