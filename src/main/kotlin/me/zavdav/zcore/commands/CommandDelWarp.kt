package me.zavdav.zcore.commands

import me.zavdav.zcore.data.Warps
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
        assert(Warps.warpExists(warpName), "warpNotFound", "warp" to warpName)

        warpName = Warps.getWarpName(warpName)
        Warps.removeWarp(warpName)
        event.sender.sendTl("deletedWarp", "warp" to warpName)
    }
}