package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.colorize
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.joinArgs
import me.zavdav.zcore.util.notifySocialSpy
import me.zavdav.zcore.util.send
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandR : AbstractCommand(
    "r",
    "Quickly replies to the last player that messaged you.",
    "/r <message>",
    "zcore.r",
    minArgs = 1,
    aliases = listOf("reply")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val user = User.from(player)
        val replyTo = user.replyTo

        if (user.checkIsMuted()) return
        assert(replyTo != null && replyTo.isOnline, "noReply")

        var message = joinArgs(args, 0)
        if (player.isAuthorized("zcore.msg.color")) message = colorize(message)
        player.send(Config.sendMsg, replyTo!!, "message" to message)

        val targetUser = User.from(replyTo)
        if (player.uniqueId !in targetUser.ignores ||
            player.isAuthorized("zcore.ignore.exempt")) {
            targetUser.replyTo = player
            replyTo.send(Config.receiveMsg, player, "message" to message)
        }

        notifySocialSpy(player, "/$name ${args.joinToString(" ")}")
    }
}