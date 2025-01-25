package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.isSelf
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

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
            target = Utils.getPlayerFromUsername(event.args[0])
        }

        val isSelf = player.isSelf(target)
        assert(isSelf || hasPermission(event.sender, "zcore.heal.others"), "noPermission")
        if (isSelf) charge(player)
        target.health = 20

        if (!isSelf) event.sender.sendTl("healedOther", target)
        target.sendTl("healed")
    }
}