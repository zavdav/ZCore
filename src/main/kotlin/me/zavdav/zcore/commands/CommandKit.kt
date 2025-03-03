package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Kits
import me.zavdav.zcore.data.Kit
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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
        val kits = Kits.getKits().filter { player.isAuthorized("zcore.kit.${it.key}") }

        if (args.isEmpty()) {
            assert(kits.isNotEmpty(), "noKits")
            sender.sendTl("kitList")
            sender.sendMessage(kits.keys.sorted().joinToString(", "))
        } else {
            val name = args[0].lowercase()
            assert(name in kits.keys, "kitNotFound", "kit" to name)
            val kit = kits[name]!!

            user.checkKitCooldowns()
            val kitCooldown = user.kitCooldowns[kit.name]
            if (kitCooldown != null) {
                assert(System.currentTimeMillis() > kitCooldown, "kitOnCooldown",
                    "name" to name,
                    "duration" to formatDuration(kitCooldown - System.currentTimeMillis()))
            }

            val currentInv = player.inventory.contents.map { it?.copy() }.toTypedArray()
            val items = kit.items.map { it.copy() }.toTypedArray()
            if (player.inventory.addItem(*items).isEmpty()) {
                try {
                    charge(player, kit)
                } catch (e: CommandException) {
                    player.inventory.contents = currentInv
                    throw e
                }
            } else {
                player.inventory.contents = currentInv
                throw CommandException(tl("noInventorySpace"))
            }

            if (kit.cooldown > 0) user.addKitCooldown(kit, kit.cooldown)
            player.sendTl("equippedKit", "name" to name)
        }
    }

    private fun charge(player: Player, kit: Kit) {
        if (kit.cost > 0.0 && !player.isAuthorized("$permission.charge.bypass")) {
            Economy.subtractBalance(player.uniqueId, kit.cost)
            player.sendTl("kitCharge", "amount" to Economy.formatBalance(kit.cost), "name" to kit.name)
        }
    }

    private fun ItemStack.copy(): ItemStack = ItemStack(this.typeId, this.amount, this.durability)
}