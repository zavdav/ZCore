package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.assert
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
        assert(isSelf || sender.isAuthorized("zcore.kill.others"), "noPermission")
        target.health = 0

        if (!isSelf) sender.sendTl("killedOther", target)
        target.sendTl("killed")
    }
}