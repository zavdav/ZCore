package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.config.Items
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.isSelf
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandGive : ZCoreCommand(
    "give",
    listOf("item", "i"),
    "Gives a player an item.",
    "/give <player> <item> [amount]",
    "zcore.give",
    true,
    2,
    3
) {

    override fun execute(event: CommandEvent) {
        val target = Utils.getPlayerFromUsername(event.args[0])

        val itemStack = Items.get(event.args[1]) ?: Items.itemFromString(event.args[1])
        assert(itemStack != null, "unknownItem")
        if (event.args.size == 3 && event.args[2].toIntOrNull() != null) {
            itemStack!!.amount = event.args[2].toInt().coerceAtLeast(1)
        }

        if (target.isSelf(event.sender as Player)) {
            event.sender.sendTl("itemGiven", "amount" to itemStack!!.amount, "item" to itemStack.type)
        } else {
            event.sender.sendTl("itemGivenOther", target, "amount" to itemStack!!.amount, "item" to itemStack.type)
        }
        target.inventory.addItem(itemStack)
    }
}