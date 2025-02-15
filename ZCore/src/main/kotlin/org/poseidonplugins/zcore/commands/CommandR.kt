package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendConfTl

class CommandR : ZCoreCommand(
    "r",
    listOf("reply"),
    "Quickly replies to the last player that messaged you.",
    "/r <message>",
    "zcore.r",
    true,
    1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val user = User.from(player)
        val replyTo = user.replyTo

        if (user.checkIsMuted()) return
        assert(replyTo != null && replyTo.isOnline, "noReply")

        var message = joinArgs(event.args, 0)
        if (hasPermission(player, "zcore.msg.color")) message = colorize(message)
        player.sendConfTl(Config.msgSendFormat, replyTo!!, "message" to message)

        val targetUser = User.from(replyTo)
        if (player.uniqueId !in targetUser.ignores ||
            hasPermission(player, "zcore.ignore.exempt")) {
            targetUser.replyTo = player
            replyTo.sendConfTl(Config.msgReceiveFormat, player, "message" to message)
        }

        Utils.notifySocialSpy(player, event.fullCommand)
    }
}