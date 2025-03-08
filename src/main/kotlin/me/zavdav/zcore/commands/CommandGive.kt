package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Items
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandGive : AbstractCommand(
    "give",
    "Gives a player an item.",
    "/give <player> <item> [amount]",
    "zcore.give",
    false,
    2,
    3,
    listOf("item", "i")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val target = getPlayerFromUsername(args[0])

        val itemStack = Items.get(args[1]) ?: Items.itemFromString(args[1])
        sender.assertOrSend("unknownItem", args[1]) { itemStack != null }
        if (args.size == 3 && args[2].toIntOrNull() != null) {
            itemStack!!.amount = args[2].toInt().coerceAtLeast(1)
        }

        val isSelf = sender is Player && sender == target
        if (isSelf) {
            sender.sendTl("gaveItem", itemStack!!.amount, itemStack.type)
        } else {
            sender.sendTl("gaveItemOther", target.name, itemStack!!.amount, itemStack.type)
        }
        target.inventory.addItem(itemStack)
    }
}