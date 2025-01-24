package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.sendTl

class CommandInvSee : Command(
    "invsee",
    description = "Shows the contents of another player's inventory.",
    usage = "/invsee <player>",
    permission = "zcore.invsee",
    isPlayerOnly = true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val user = User.from(player)

        if (event.args.isNotEmpty()) {
            val target = Utils.getPlayerFromUsername(event.args[0])
            if (user.savedInventory == null) user.savedInventory = player.inventory.contents
            player.inventory.contents = User.from(target).savedInventory ?: target.inventory.contents
            event.sender.sendTl("lookingAtInventory", target)
        } else {
            if (user.savedInventory != null) {
                player.inventory.contents = user.savedInventory
                user.savedInventory = null
            }
            event.sender.sendTl("inventoryRestored")
        }
    }
}