package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandInvSee : AbstractCommand(
    "invsee",
    "Shows the contents of a player's inventory.",
    "/invsee <player>",
    "zcore.invsee",
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val user = User.from(player)

        if (args.isNotEmpty()) {
            val target = getPlayerFromUsername(args[0])
            if (user.savedInventory == null) user.savedInventory = player.inventory.contents
            player.inventory.contents = User.from(target).savedInventory ?: target.inventory.contents
            user.isInvSee = true
            sender.sendTl("lookingAtInventory", target.name)
        } else {
            if (user.savedInventory != null) {
                player.inventory.contents = user.savedInventory
                user.savedInventory = null
            }
            user.isInvSee = false
            sender.sendTl("restoredInventory")
        }
    }
}