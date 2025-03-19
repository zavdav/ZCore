package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Items
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandItem : AbstractCommand(
    "item",
    "Gives you an item.",
    "/item <item> [amount]",
    "zcore.item",
    minArgs = 1,
    maxArgs = 2,
    aliases = listOf("i")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player

        val itemStack = Items.get(args[0]) ?: Items.itemFromString(args[0])
        sender.assertOrSend("unknownItem", args[0]) { itemStack != null }
        if (args.size == 2 && args[1].toIntOrNull() != null) {
            itemStack!!.amount = args[1].toInt().coerceAtLeast(1)
        }

        sender.sendTl("gaveItem", itemStack!!.amount, itemStack.type)
        player.inventory.addItem(itemStack)
    }
}