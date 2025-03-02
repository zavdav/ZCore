package me.zavdav.zcore.commands

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.getMessage
import me.zavdav.zcore.util.send
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender

class CommandZCore : AbstractCommand(
    "zcore",
    "Displays information about ZCore.",
    "/zcore",
    "zcore.zcore",
    false
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val asciiArt = getMessage("zcoreAsciiArt")
        asciiArt.split("\n").forEach { sender.send(it) }
        sender.sendTl("zcoreVersion", "version" to ZCore.INSTANCE.description.version)
    }
}