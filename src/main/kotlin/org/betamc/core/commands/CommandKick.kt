package org.betamc.core.commands

import org.betamc.core.config.Language
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*

class CommandKick : Command(
    "kick",
    description = "Kicks a player from the server.",
    usage = "/kick <player> [message]",
    permission = "bmc.kick",
    minArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player: Player? = Bukkit.matchPlayer(event.args[0])?.get(0)
        if (player == null) {
            sendMessage(event.sender, Language.PLAYER_NOT_FOUND.msg
                .replace("%player%", event.args[0]))
            return
        }
        player.kickPlayer(if (event.args.size > 1) colorize(joinArgs(event.args, 1)) else Language.KICK_DEFAULT_MESSAGE.msg)
    }
}