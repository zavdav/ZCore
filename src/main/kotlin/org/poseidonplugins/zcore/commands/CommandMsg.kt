package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.sendConfTl

class CommandMsg : ZCoreCommand(
    "msg",
    listOf("m", "tell", "t", "whisper", "w"),
    "Sends a private message to a player.",
    "/msg <player> <message>",
    "zcore.msg",
    true,
    2
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val target = Utils.getPlayerFromUsername(event.args[0])
        var message = joinArgs(event.args, 1)
        if (hasPermission(player, "zcore.msg.color")) message = colorize(message)

        User.from(player).replyTo = target
        player.sendConfTl("msgSendFormat", target, "message" to message)
        val user = User.from(target)

        if (player.uniqueId !in user.ignores ||
            hasPermission(player, "zcore.ignore.exempt")) {
            user.replyTo = player
            target.sendConfTl("msgReceiveFormat", player, "message" to message)
        }
    }
}