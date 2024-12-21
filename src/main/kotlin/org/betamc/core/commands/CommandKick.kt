package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.config.Property
import org.betamc.core.util.Utils
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
            sendMessage(event.sender, Utils.format(Language.PLAYER_NOT_FOUND, event.args[0]))
            return
        }
        val reason = colorize(if (event.args.size > 1) joinArgs(event.args, 1)
            else Property.KICK_DEFAULT_REASON.toString())

        target.kickPlayer(Utils.format(Property.KICK_FORMAT, reason))
        sendMessage(event.sender, Utils.format(Language.KICK_SUCCESS, target.name, reason))
    }
}