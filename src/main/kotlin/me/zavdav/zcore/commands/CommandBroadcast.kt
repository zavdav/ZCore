package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.broadcast
import me.zavdav.zcore.util.colorize
import me.zavdav.zcore.util.joinArgs
import org.bukkit.command.CommandSender

class CommandBroadcast : AbstractCommand(
    "broadcast",
    "Broadcasts a message to all players.",
    "/broadcast <message>",
    "zcore.broadcast",
    false,
    1,
    aliases = listOf("bc")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        broadcast(Config.broadcast, "message" to colorize(joinArgs(args, 0)))
    }
}