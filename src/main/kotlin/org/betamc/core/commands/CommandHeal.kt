package org.betamc.core.commands

import org.betamc.core.util.Utils
import org.betamc.core.util.Utils.isSelf
import org.betamc.core.util.format
import org.betamc.core.util.formatError
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage

class CommandHeal : Command(
    "heal",
    description = "Heals a player to full health.",
    usage = "/heal [player]",
    permission = "bmc.heal",
    isPlayerOnly = true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        var target: Player? = event.sender as Player
        if (event.args.isNotEmpty()) {
            target = Utils.getPlayerFromUsername(event.args[0])
            if (target == null) {
                sendMessage(event.sender, formatError("playerNotFound",
                    "player" to event.args[0]))
                return
            }
        }

        val isSelf = (event.sender as Player).isSelf(target!!)
        if (!isSelf && !hasPermission(event.sender, "bmc.heal.others")) {
            sendMessage(event.sender, format("noPermission"))
            return
        }
        target.health = 20

        if (isSelf) {
            sendMessage(event.sender, format("healed"))
        } else {
            sendMessage(event.sender, format("healedOther",
                "player" to target.name))
            sendMessage(target, format("healed"))
        }
    }
}