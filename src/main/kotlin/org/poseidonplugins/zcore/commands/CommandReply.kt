package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendConfTl

class CommandReply : Command(
    "reply",
    listOf("r"),
    "Quickly replies to the last player that messaged you.",
    "/reply <message>",
    "zcore.reply",
    true,
    1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val zPlayer = PlayerMap.getPlayer(player)
        val replyTo = zPlayer.replyTo
        assert(replyTo != null && replyTo.isOnline, "noReply")

        var message = joinArgs(event.args, 0)
        if (hasPermission(player, "zcore.msg.color")) message = colorize(message)
        player.sendConfTl("msgSendFormat", replyTo!!, "message" to message)

        val zTarget = PlayerMap.getPlayer(replyTo)
        if (player.uniqueId !in zTarget.ignores ||
            hasPermission(player, "zcore.ignore.exempt")) {
            zTarget.replyTo = player
            replyTo.sendConfTl("msgReceiveFormat", player, "message" to message)
        }
    }
}