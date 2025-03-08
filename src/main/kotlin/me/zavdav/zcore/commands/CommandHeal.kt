package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHeal : AbstractCommand(
    "heal",
    "Heals a player to full health.",
    "/heal [player]",
    "zcore.heal",
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var target = player
        if (args.isNotEmpty()) {
            target = getPlayerFromUsername(args[0])
        }

        val isSelf = player == target
        sender.assertOrSend("noPermission") { isSelf || it.isAuthorized("zcore.heal.others") }
        if (isSelf) charge(player)
        target.health = 20

        if (!isSelf) sender.sendTl("healedOther", target.name)
        target.sendTl("healed")
    }
}