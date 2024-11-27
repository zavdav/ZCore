package org.betamc.core.commands

import org.betamc.core.config.Language
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.joinArgs

class CommandKickAll : Command(
    "kickall",
    description = "Kicks all players from the server.",
    usage = "/kickall [message]",
    permission = "bmc.kickall",
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (event.sender !is Player || !player.equals(event.sender as Player)) {
                player.kickPlayer(if (event.args.isNotEmpty()) colorize(joinArgs(event.args, 0)) else Language.KICK_DEFAULT_MESSAGE.msg)
            }
        }
    }
}