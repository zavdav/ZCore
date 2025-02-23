package me.zavdav.zcore.commands

import me.zavdav.zcore.data.Warps
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

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
        assert(!Warps.warpExists(warpName), "warpAlreadyExists", "warp" to warpName)

        Warps.setWarp(warpName, (event.sender as Player).location)
        event.sender.sendTl("setWarp", "warp" to warpName)
    }
}