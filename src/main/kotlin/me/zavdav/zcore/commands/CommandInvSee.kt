package me.zavdav.zcore.commands

import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandInvSee : ZCoreCommand(
    "invsee",
    description = "Shows the contents of a player's inventory.",
    usage = "/invsee <player>",
    permission = "zcore.invsee",
    isPlayerOnly = true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val user = User.from(player)

        if (event.args.isNotEmpty()) {
            val target = getPlayerFromUsername(event.args[0])
            if (user.savedInventory == null) user.savedInventory = player.inventory.contents
            player.inventory.contents = User.from(target).savedInventory ?: target.inventory.contents
            user.isInvSee = true
            event.sender.sendTl("lookingAtInventory", target)
        } else {
            if (user.savedInventory != null) {
                player.inventory.contents = user.savedInventory
                user.savedInventory = null
            }
            user.isInvSee = false
            event.sender.sendTl("restoredInventory")
        }
    }
}