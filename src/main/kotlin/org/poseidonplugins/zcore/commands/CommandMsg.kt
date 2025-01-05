package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.formatProperty

class CommandMsg : Command(
    "msg",
    listOf("m", "tell", "t", "whisper", "w"),
    "Sends a private message to a player.",
    "/msg <player> <message>",
    "zcore.msg",
    true,
    2,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val target = Utils.getPlayerFromUsername(event.args[0])
        var message = joinArgs(event.args, 1)

        if (hasPermission(event.sender, "zcore.msg.color")) {
            message = colorize(message)
        }

        event.sender.sendMessage(formatProperty("msgSendFormat", target, "message" to message))
        target.sendMessage(formatProperty("msgReceiveFormat", event.sender as Player, "message" to message))
    }
}