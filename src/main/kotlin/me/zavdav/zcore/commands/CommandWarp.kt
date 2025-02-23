package me.zavdav.zcore.commands

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.Warps
import me.zavdav.zcore.util.Delay
import me.zavdav.zcore.util.NoFundsException
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandWarp : ZCoreCommand(
    "warp",
    description = "Teleports you to the specified warp.",
    usage = "/warp [name]",
    permission = "zcore.warp",
    isPlayerOnly = true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            event.sender.sendTl("warpList")
            event.sender.sendMessage(Warps.getWarps().sorted().joinToString(", "))
        } else {
            val player = event.sender as Player
            var warpName = event.args[0]
            assert(Warps.warpExists(warpName), "warpNotFound", "warp" to warpName)

            val location = Warps.getWarpLocation(warpName)
            val delay = Config.teleportDelay
            warpName = Warps.getWarpName(warpName)

            if (delay > 0) {
                event.sender.sendTl("commencingTeleport", "location" to warpName, "delay" to delay)
                event.sender.sendTl("doNotMove")
            }
            Delay(player, delay) {
                try {
                    charge(player)
                    player.teleport(location)
                    event.sender.sendTl("teleportedToWarp", "warp" to warpName)
                } catch (e: NoFundsException) {
                    for (message in e.messages) {
                        player.sendMessage(message)
                    }
                }
            }
        }
    }
}