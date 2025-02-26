package me.zavdav.zcore.commands

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.notifySocialSpy
import me.zavdav.zcore.util.send
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.joinArgs

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
        player.send(Config.sendMsg, replyTo!!, "message" to message)

        val targetUser = User.from(replyTo)
        if (player.uniqueId !in targetUser.ignores ||
            hasPermission(player, "zcore.ignore.exempt")) {
            targetUser.replyTo = player
            replyTo.send(Config.receiveMsg, player, "message" to message)
        }

        notifySocialSpy(player, event.fullCommand)
    }
}