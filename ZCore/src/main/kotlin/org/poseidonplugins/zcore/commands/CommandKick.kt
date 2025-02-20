package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.tl
import org.poseidonplugins.zcore.util.kick
import org.poseidonplugins.zcore.util.sendTl

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