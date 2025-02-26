package me.zavdav.zcore.commands

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.notifySocialSpy
import me.zavdav.zcore.util.send
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.joinArgs

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
        val target = getPlayerFromUsername(event.args[0])
        val user = User.from(player)

        if (user.checkIsMuted()) return
        var message = joinArgs(event.args, 1)
        if (hasPermission(player, "zcore.msg.color")) message = colorize(message)

        user.replyTo = target
        player.send(Config.sendMsg, target, "message" to message)
        val targetUser = User.from(target)

        if (player.uniqueId !in targetUser.ignores ||
            hasPermission(player, "zcore.ignore.exempt")) {
            targetUser.replyTo = player
            target.send(Config.receiveMsg, player, "message" to message)
        }

        notifySocialSpy(player, event.fullCommand)
    }
}