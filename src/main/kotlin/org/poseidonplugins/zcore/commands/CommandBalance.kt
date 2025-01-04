package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format

class CommandBalance : Command(
    "balance",
    listOf("bal"),
    "Shows your or a player's balance.",
    "/balance [player/uuid]",
    "zcore.balance",
    true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var uuid = player.uniqueId
        if (event.args.isNotEmpty()) {
            uuid = Utils.getUUIDFromString(event.args[0])
        }

        val isSelf = player.uniqueId == uuid
        if (!isSelf && !hasPermission(event.sender, "zcore.balance.others")) {
            event.sender.sendMessage(format("noPermission"))
            return
        }

        val amount = Economy.getBalance(uuid)
        val name = PlayerMap.getPlayer(uuid).name
        event.sender.sendMessage(if (isSelf)
            format("balance", "amount" to Economy.formatBalance(amount))
            else format("balanceOther",
                "user" to name, "amount" to Economy.formatBalance(amount)))
    }
}