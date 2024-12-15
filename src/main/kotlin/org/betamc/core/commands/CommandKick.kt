package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.util.Utils
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
        val player: Player? = Utils.getPlayerFromUsername(event.args[0])
        if (player == null) {
            sendMessage(event.sender, Utils.format(Language.PLAYER_NOT_FOUND, event.args[0]))
            return
        }
        val message = if (event.args.size > 1) colorize(joinArgs(event.args, 1)) else Language.KICK_DEFAULT_MESSAGE.msg
        player.kickPlayer(message)
        broadcastMessage(Language.KICK_MESSAGE_BROADCAST.msg
            .replace("%sender%", event.sender.name)
            .replace("%player%", player.name)
            .replace("%message%", message), "bmc.kick.see-messages")
    }
}