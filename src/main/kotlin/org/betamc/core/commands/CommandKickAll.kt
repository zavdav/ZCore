package org.betamc.core.commands

import org.betamc.core.config.Property
import org.betamc.core.util.Utils.safeSubstring
import org.betamc.core.util.format
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
        val reason = colorize(if (event.args.isNotEmpty()) joinArgs(event.args, 0)
            else Property.KICK_DEFAULT_REASON.toString())

        for (player in Bukkit.getOnlinePlayers()) {
            if (event.sender !is Player || !player.equals(event.sender as Player)) {
                player.kickPlayer(format(Property.KICK_FORMAT,
                    "reason" to reason).safeSubstring(0, 99))
            }
        }
        sendMessage(event.sender, format("allKicked",
            "reason" to reason))
    }
}