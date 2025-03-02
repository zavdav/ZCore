package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.colorize
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.joinArgs
import me.zavdav.zcore.util.kick
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender

class CommandKick : AbstractCommand(
    "kick",
    "Kicks a player from the server.",
    "/kick <player> [message]",
    "zcore.kick",
    false,
    1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val target = getPlayerFromUsername(args[0])
        assert(sender == target || !target.isAuthorized("zcore.kick.exempt"),
            "cannotKickPlayer", "name" to target.name)
        val reason = colorize(if (args.size > 1) joinArgs(args, 1) else tl("kickReason"))

        target.kick("kickScreen", "reason" to reason)
        sender.sendTl("kickedPlayer", "player" to target.name, "reason" to reason)
    }
}