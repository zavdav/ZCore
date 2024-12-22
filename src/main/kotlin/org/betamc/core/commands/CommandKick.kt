package org.betamc.core.commands

import org.betamc.core.config.Property
import org.betamc.core.util.Utils
import org.betamc.core.util.Utils.safeSubstring
import org.betamc.core.util.format
import org.betamc.core.util.formatError
import org.poseidonplugins.commandapi.*

class CommandKick : Command(
    "kick",
    description = "Kicks a player from the server.",
    usage = "/kick <player> [message]",
    permission = "bmc.kick",
    minArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val target = Utils.getPlayerFromUsername(event.args[0])
        if (target == null) {
            sendMessage(event.sender, formatError("playerNotFound",
                "player" to event.args[0]))
            return
        }
        val reason = colorize(if (event.args.size > 1) joinArgs(event.args, 1)
            else Property.KICK_DEFAULT_REASON.toString())

        target.kickPlayer(format(Property.KICK_FORMAT,
            "reason" to reason).safeSubstring(0, 99))
        sendMessage(event.sender, format("playerKicked",
            "player" to target.name,
            "reason" to reason).safeSubstring(0, 99))
    }
}