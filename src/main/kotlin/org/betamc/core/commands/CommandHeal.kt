package org.betamc.core.commands

import org.betamc.core.config.Language
import org.bukkit.Bukkit
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
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            if (event.sender !is Player) {
                sendMessage(event.sender, Language.PLAYER_ONLY)
                return
            }

            (event.sender as Player).health = 20
            sendMessage(event.sender, Language.HEAL_SELF)
        } else if (hasPermission(event.sender, "bmc.heal.others")) {
            val player: Player? = Bukkit.matchPlayer(event.args[0]).getOrNull(0)
            if (player == null) {
                sendMessage(event.sender, Language.PLAYER_NOT_FOUND.msg
                    .replace("%player%", event.args[0]))
                return
            }

            player.health = 20
            if (!event.sender.name.equals(player.name, true)) {
                sendMessage(event.sender, Language.HEAL_PLAYER.msg
                    .replace("%player%", player.name))
            }
            sendMessage(player, Language.HEAL_SELF)
        } else {
            sendMessage(event.sender, Language.NO_PERMISSION)
        }
    }
}