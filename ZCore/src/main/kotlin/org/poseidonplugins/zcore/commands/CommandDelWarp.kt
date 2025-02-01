package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.WarpData
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandDelWarp : ZCoreCommand(
    "delwarp",
    description = "Deletes the specified warp.",
    usage = "/delwarp <name>",
    permission = "zcore.delwarp",
    isPlayerOnly = true,
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        var warpName = event.args[0]
        assert(WarpData.warpExists(warpName), "warpNotFound")

        warpName = WarpData.getWarpName(warpName)
        WarpData.removeWarp(warpName)
        event.sender.sendTl("warpDeleted", "warp" to warpName)
    }
}