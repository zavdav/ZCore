package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpaHere : AbstractCommand(
    "tpahere",
    "Sends a request for a player to teleport to you.",
    "/tpahere <player>",
    "zcore.tpahere",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val target = getPlayerFromUsername(args[0])
        val targetUser = User.from(target)
        player.sendTl("sentTpRequest", target.name)

        if (player.uniqueId !in targetUser.ignores ||
            player.isAuthorized("zcore.ignore.exempt")) {
            targetUser.tpRequest = player to User.TeleportType.TPAHERE
            target.sendTl("tpaHereRequest", player.name)
            target.sendTl("tpaUsage")
        }
    }
}