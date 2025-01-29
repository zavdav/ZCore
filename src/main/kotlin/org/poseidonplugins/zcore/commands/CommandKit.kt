package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.config.Kits
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.*
import org.poseidonplugins.zcore.util.Utils.copy
import java.time.LocalDateTime

class CommandKit : ZCoreCommand(
    "kit",
    description = "Gives you the specified kit.",
    usage = "/kit [name]",
    permission = "zcore.kit",
    isPlayerOnly = true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val user = User.from(player)
        val kits = Kits.getKits().filter { hasPermission(player, "zcore.kit.${it.key}") }

        if (event.args.isEmpty()) {
            assert(kits.isNotEmpty(), "noKitsSet")
            event.sender.sendTl("kitList")
            event.sender.sendMessage(kits.keys.sorted().joinToString(", "))
        } else {
            val name = event.args[0].lowercase()
            assert(name in kits.keys, "kitNotFound")
            val kit = kits[name]!!

            user.checkKitCooldowns()
            val kitCooldown = user.kitCooldowns[kit]
            if (kitCooldown != null) {
                assert(LocalDateTime.now().isAfter(kitCooldown), "kitOnCooldown",
                    "name" to name,
                    "duration" to Utils.formatDateDiff(LocalDateTime.now(), kitCooldown))
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
                throw CommandException(formatError("noInventorySpace"))
            }

            if (kit.cooldown > 0) user.addKitCooldown(kit, kit.cooldown)
            player.sendTl("kitEquipped", "name" to name)
        }
    }

    private fun charge(player: Player, kit: Kits.Kit) {
        if (kit.cost > 0.0 && !hasPermission(player, "$permission.charge.bypass")) {
            Economy.subtractBalance(player.uniqueId, kit.cost)
            player.sendTl("kitCharge", "amount" to Economy.formatBalance(kit.cost), "name" to kit.name)
        }
    }
}