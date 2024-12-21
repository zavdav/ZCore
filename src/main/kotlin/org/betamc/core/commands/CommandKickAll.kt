package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.config.Property
import org.betamc.core.util.Utils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*

class CommandKickAll : Command(
    "kickall",
    description = "Kicks all players from the server.",
    usage = "/kickall [message]",
    permission = "bmc.kickall",
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val reason = colorize(if (event.args.size > 1) joinArgs(event.args, 0)
            else Property.KICK_DEFAULT_REASON.toString())

        for (player in Bukkit.getOnlinePlayers()) {
            if (event.sender !is Player || !player.equals(event.sender as Player)) {
                player.kickPlayer(Utils.format(Property.KICK_FORMAT, reason))
            }
        }
        sendMessage(event.sender, Utils.format(Language.KICKALL_SUCCESS, reason))
    }
}