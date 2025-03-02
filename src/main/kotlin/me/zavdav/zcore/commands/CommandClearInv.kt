package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandClearInv : AbstractCommand(
    "clearinv",
    "Clears your inventory.",
    "/clearinv [player]",
    "zcore.clearinv",
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var target = player
        if (args.isNotEmpty()) {
            target = getPlayerFromUsername(args[0])
        }

        val isSelf = player == target
        assert(isSelf || sender.isAuthorized("zcore.clearinv.others"), "noPermission")
        if (isSelf) charge(player)
        target.inventory.clear()

        if (!isSelf) sender.sendTl("clearedInventoryOther", target)
        target.sendTl("clearedInventory")
    }
}