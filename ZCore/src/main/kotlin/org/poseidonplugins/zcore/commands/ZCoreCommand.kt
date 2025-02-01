package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.sendTl

abstract class ZCoreCommand(
    name: String,
    aliases: List<String> = emptyList(),
    description: String,
    usage: String,
    permission: String,
    isPlayerOnly: Boolean = false,
    minArgs: Int = 0,
    maxArgs: Int = -1
) : Command(
    name, aliases, description, usage, permission,
    isPlayerOnly, minArgs, maxArgs, Preprocessor()
) {

    val cost: Double
        get() = Config.getCommandCost(this)

    protected open fun charge(player: Player) {
        if (cost > 0.0 && !hasPermission(player, "$permission.charge.bypass")) {
            Economy.subtractBalance(player.uniqueId, cost)
            player.sendTl("commandCharge",
                "amount" to Economy.formatBalance(cost),
                "command" to name.replace("\\.".toRegex(), " "))
        }
    }
}