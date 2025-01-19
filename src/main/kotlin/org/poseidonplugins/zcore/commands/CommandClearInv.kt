package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.isSelf
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandClearInv : Command(
    "clearinv",
    description = "Clears your inventory.",
    usage = "/clearinv [player]",
    permission = "zcore.clearinv",
    isPlayerOnly = true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        var target = event.sender as Player
        if (event.args.isNotEmpty()) {
            target = Utils.getPlayerFromUsername(event.args[0])
        }

        val isSelf = (event.sender as Player).isSelf(target)
        assert(isSelf || hasPermission(event.sender, "zcore.clearinv.others"), "noPermission")
        target.inventory.clear()

        if (!isSelf) event.sender.sendTl("inventoryClearedOther", target)
        target.sendTl("inventoryCleared")
    }
}