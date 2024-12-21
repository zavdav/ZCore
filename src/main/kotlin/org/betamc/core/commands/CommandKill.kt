package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.util.Utils
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage

class CommandKill : Command(
    "kill",
    description = "Kills a player.",
    usage = "/kill [player]",
    permission = "bmc.kill",
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
        target!!.health = 0

        val isSelf = (event.sender as Player).uniqueId == target.uniqueId
        sendMessage(event.sender, Utils.format(Language.KILL_SUCCESS,
            if (isSelf) "You have" else "${target.name} has"))
    }
}