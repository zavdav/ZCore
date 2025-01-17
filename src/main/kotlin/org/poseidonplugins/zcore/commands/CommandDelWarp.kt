package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.WarpData
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandDelWarp : Command(
    "delwarp",
    description = "Deletes the specified warp.",
    usage = "/delwarp <name>",
    permission = "zcore.delwarp",
    isPlayerOnly = true,
    minArgs = 1,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val warpName = event.args[0]
        assert(WarpData.warpExists(warpName), "warpNotFound")

        val finalName = WarpData.getFinalWarpName(warpName)
        WarpData.removeWarp(finalName)
        event.sender.sendTl("warpDeleted", "warp" to finalName)
    }
}