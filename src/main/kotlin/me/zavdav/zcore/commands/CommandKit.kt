package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.api.EconomyException
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Kit
import me.zavdav.zcore.config.Kits
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

class CommandKit : AbstractCommand(
    "kit",
    "Gives you the specified kit.",
    "/kit [name]",
    "zcore.kit",
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val user = User.from(player)
        val kits = Kits.getKits()

        if (args.isEmpty()) {
            sender.assertOrSend("noKits") { kits.isNotEmpty() }
            sender.sendTl("kitList")
            sender.sendMessage(kits.map { it.name }.sorted().joinToString(", "))
        } else {
            val kit = Kits.getKit(args[0])
            sender.assertOrSend("kitNotFound", args[0]) { kit != null }

            user.checkKitCooldowns()
            val kitCooldown = user.kitCooldowns[kit!!.name]
            if (kitCooldown != null) {
                sender.assertOrSend("kitOnCooldown", kit.name,
                                    formatDuration(kitCooldown - System.currentTimeMillis()))
                { System.currentTimeMillis() > kitCooldown }
            }

            val currentInv = player.inventory.contents.map { it?.copy() }.toTypedArray()
            val items = kit.items.map { it.copy() }.toTypedArray()
            if (player.inventory.addItem(*items).isEmpty()) {
                try {
                    charge(player, kit)
                } catch (e: EconomyException) {
                    player.inventory.contents = currentInv
                    throw e
                }
            } else {
                player.inventory.contents = currentInv
                throw CommandException(sender, tl("noInventorySpace"))
            }

            if (kit.cooldown > 0) user.addKitCooldown(kit, kit.cooldown)
            player.sendTl("equippedKit", name)
        }
    }

    private fun charge(player: Player, kit: Kit) {
        if (kit.cost > BigDecimal.ZERO && !player.isAuthorized("$permission.charge.bypass")) {
            Economy.subtractBalance(player.uniqueId, kit.cost)
            player.sendTl("kitCharge", Economy.formatBalance(kit.cost), kit.name)
        }
    }

    private fun ItemStack.copy(): ItemStack = ItemStack(typeId, amount, durability)
}