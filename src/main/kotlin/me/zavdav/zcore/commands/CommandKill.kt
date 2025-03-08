package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandKill : AbstractCommand(
    "kill",
    "Kills a player.",
    "/kill [player]",
    "zcore.kill",
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var target = player
        if (args.isNotEmpty()) {
            target = getPlayerFromUsername(args[0])
        }

        val isSelf = player == target
        sender.assertOrSend("noPermission") { isSelf || it.isAuthorized("zcore.kill.others") }
        target.health = 0

        if (!isSelf) sender.sendTl("killedOther", target.name)
        target.sendTl("killed")
    }
}