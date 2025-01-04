package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.isSelf
import org.poseidonplugins.zcore.util.format

class CommandHeal : Command(
    "heal",
    description = "Heals a player to full health.",
    usage = "/heal [player]",
    permission = "zcore.heal",
    isPlayerOnly = true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        var target = event.sender as Player
        if (event.args.isNotEmpty()) {
            target = Utils.getPlayerFromUsername(event.args[0])
        }

        val isSelf = (event.sender as Player).isSelf(target)
        if (!isSelf && !hasPermission(event.sender, "zcore.heal.others")) {
            event.sender.sendMessage(format("noPermission"))
            return
        }

        target.health = 20
        if (!isSelf) event.sender.sendMessage(format("healedOther", target))
        target.sendMessage(format("healed"))
    }
}