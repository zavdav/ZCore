package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.data.WarpData
import org.poseidonplugins.zcore.util.Delay
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandWarp : Command(
    "warp",
    description = "Teleports you to the specified warp.",
    usage = "/warp [name]",
    permission = "zcore.warp",
    isPlayerOnly = true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.sender.sendTl("warpList")
            event.sender.sendMessage(WarpData.getWarps().sorted().joinToString(", "))
        } else {
            val player = event.sender as Player
            val warpName = event.args[0]
            assert(WarpData.warpExists(warpName), "warpNotFound")

            val location = WarpData.getWarp(warpName)
            val delay = Config.getInt("teleportDelay")
            val finalName = WarpData.getFinalWarpName(warpName)

            if (delay > 0) {
                event.sender.sendTl("commencingTeleport", "location" to finalName, "delay" to delay)
                event.sender.sendTl("doNotMove")
                Delay(player, {
                    player.teleport(location)
                    event.sender.sendTl("teleportedToWarp", "warp" to finalName)
                }, delay)
            } else {
                player.teleport(location)
                event.sender.sendTl("teleportedToWarp", "warp" to finalName)
            }
        }
    }
}