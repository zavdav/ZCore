package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.player.PlayerMap
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
        val player = event.sender as Player
        val target = Utils.getPlayerFromUsername(event.args[0])
        var message = joinArgs(event.args, 1)

        if (hasPermission(player, "zcore.msg.color")) {
            message = colorize(message)
        }

        PlayerMap.getPlayer(player).replyTo = target
        player.sendMessage(formatProperty("msgSendFormat", target, "message" to message))

        val zPlayer = PlayerMap.getPlayer(target)
        if (player.uniqueId !in zPlayer.ignores ||
            hasPermission(player, "zcore.ignore.exempt")) {
            zPlayer.replyTo = player
            target.sendMessage(formatProperty("msgReceiveFormat", player, "message" to message))
        }
    }
}