package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.WarpData
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandSetWarp : ZCoreCommand(
    "setwarp",
    description = "Sets a new warp at your current location.",
    usage = "/setwarp <name>",
    permission = "zcore.setwarp",
    isPlayerOnly = true,
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val warpName = event.args[0]
        assert(warpName.matches("^[a-zA-Z0-9_-]+$".toRegex()), "invalidWarpName")
        assert(!WarpData.warpExists(warpName), "warpAlreadyExists")

        WarpData.setWarp(warpName, (event.sender as Player).location)
        event.sender.sendTl("warpSet", "warp" to warpName)
    }
}