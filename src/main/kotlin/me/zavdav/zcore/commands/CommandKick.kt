package me.zavdav.zcore.commands

import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.kick
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.joinArgs

class CommandKick : ZCoreCommand(
    "kick",
    description = "Kicks a player from the server.",
    usage = "/kick <player> [message]",
    permission = "zcore.kick",
    minArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val target = Utils.getPlayerFromUsername(event.args[0])
        val reason = colorize(if (event.args.size > 1) joinArgs(event.args, 1)
            else tl("kickReason"))

        target.kick("kickScreen", "reason" to reason)
        event.sender.sendTl("kickedPlayer", "player" to target.name, "reason" to reason)
    }
}