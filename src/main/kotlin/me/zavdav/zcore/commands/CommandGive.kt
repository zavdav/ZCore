package me.zavdav.zcore.commands

import me.zavdav.zcore.config.Items
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandGive : ZCoreCommand(
    "give",
    listOf("item", "i"),
    "Gives a player an item.",
    "/give <player> <item> [amount]",
    "zcore.give",
    minArgs = 2,
    maxArgs = 3
) {

    override fun execute(event: CommandEvent) {
        val target = getPlayerFromUsername(event.args[0])

        val itemStack = Items.get(event.args[1]) ?: Items.itemFromString(event.args[1])
        assert(itemStack != null, "unknownItem", "item" to event.args[1])
        if (event.args.size == 3 && event.args[2].toIntOrNull() != null) {
            itemStack!!.amount = event.args[2].toInt().coerceAtLeast(1)
        }

        val isSelf = event.sender is Player && event.sender == target
        if (isSelf) {
            event.sender.sendTl("gaveItem", "amount" to itemStack!!.amount, "item" to itemStack.type)
        } else {
            event.sender.sendTl("gaveItemOther", target, "amount" to itemStack!!.amount, "item" to itemStack.type)
        }
        target.inventory.addItem(itemStack)
    }
}