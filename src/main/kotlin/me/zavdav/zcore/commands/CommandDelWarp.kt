package me.zavdav.zcore.commands

import me.zavdav.zcore.data.WarpData
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.poseidonplugins.commandapi.CommandEvent

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
        assert(WarpData.warpExists(warpName), "warpNotFound", "warp" to warpName)

        warpName = WarpData.getWarpName(warpName)
        WarpData.removeWarp(warpName)
        event.sender.sendTl("deletedWarp", "warp" to warpName)
    }
}