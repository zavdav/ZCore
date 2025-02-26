package me.zavdav.zcore.commands

import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission

class CommandHeal : ZCoreCommand(
    "heal",
    description = "Heals a player to full health.",
    usage = "/heal [player]",
    permission = "zcore.heal",
    isPlayerOnly = true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var target = player
        if (event.args.isNotEmpty()) {
            target = getPlayerFromUsername(event.args[0])
        }

        val isSelf = player == target
        assert(isSelf || hasPermission(event.sender, "zcore.heal.others"), "noPermission")
        if (isSelf) charge(player)
        target.health = 20

        if (!isSelf) event.sender.sendTl("healedOther", target)
        target.sendTl("healed")
    }
}