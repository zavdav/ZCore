package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.util.Utils
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
                sendMessage(event.sender, Utils.format(Language.PLAYER_NOT_FOUND, event.args[0]))
                return
            }
        }

        val isSelf = (event.sender as Player).uniqueId == target!!.uniqueId
        if (!isSelf && !hasPermission(event.sender, "bmc.heal.others")) {
            sendMessage(event.sender, Language.NO_PERMISSION)
            return
        }
        target.health = 20

        if (isSelf) {
            sendMessage(event.sender, Utils.format(Language.HEAL_SUCCESS, "You have"))
        } else {
            sendMessage(event.sender, Utils.format(Language.HEAL_SUCCESS, "${target.name} has"))
            sendMessage(target, Utils.format(Language.HEAL_SUCCESS, "You have"))
        }
    }
}