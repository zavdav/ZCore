package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.config.Property
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.safeSubstring
import org.poseidonplugins.zcore.util.format

class CommandKick : Command(
    "kick",
    description = "Kicks a player from the server.",
    usage = "/kick <player> [message]",
    permission = "zcore.kick",
    minArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val target = Utils.getPlayerFromUsername(event.args[0])
        val reason = colorize(if (event.args.size > 1) joinArgs(event.args, 1)
            else Property.KICK_DEFAULT_REASON.toString())

        target.kickPlayer(format(Property.KICK_FORMAT,
            "reason" to reason).safeSubstring(0, 99))
        sendMessage(event.sender, format("playerKicked",
            "player" to target.name,
            "reason" to reason).safeSubstring(0, 99))
    }
}