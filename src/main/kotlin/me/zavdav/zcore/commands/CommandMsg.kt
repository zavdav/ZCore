package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.colorize
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.joinArgs
import me.zavdav.zcore.util.notifySocialSpy
import me.zavdav.zcore.util.send
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandMsg : AbstractCommand(
    "msg",
    "Sends a private message to a player.",
    "/msg <player> <message>",
    "zcore.msg",
    minArgs = 2,
    aliases = listOf("m", "tell", "t", "whisper", "w")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val target = getPlayerFromUsername(args[0])
        val user = User.from(player)

        if (user.checkIsMuted()) return
        var message = joinArgs(args, 1)
        if (player.isAuthorized("zcore.msg.color")) message = colorize(message)

        user.replyTo = target
        player.send(Config.sendMsg,
            "name" to target.name, "displayname" to target.displayName, "message" to message)
        val targetUser = User.from(target)

        if (player.uniqueId !in targetUser.ignores ||
            player.isAuthorized("zcore.ignore.exempt")) {
            targetUser.replyTo = player
            target.send(Config.receiveMsg,
                "name" to player.name, "displayname" to player.displayName, "message" to message)
        }

        notifySocialSpy(player, "/$name ${args.joinToString(" ")}")
    }
}