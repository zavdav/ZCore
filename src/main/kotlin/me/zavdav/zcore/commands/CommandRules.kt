package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.assertOrSend
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
        sender.assertOrSend("noRules") { Config.rules.isNotEmpty() }
        Config.rules.forEach { sender.send(it) }
    }
}