package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.Warps
import me.zavdav.zcore.util.Delay
import me.zavdav.zcore.util.NoFundsException
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandWarp : AbstractCommand(
    "warp",
    "Teleports you to the specified warp.",
    "/warp [name]",
    "zcore.warp",
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendTl("warpList")
            sender.sendMessage(Warps.getWarps().sorted().joinToString(", "))
        } else {
            val player = sender as Player
            var warpName = args[0]
            assert(Warps.warpExists(warpName), "warpNotFound", warpName)

            val location = Warps.getWarpLocation(warpName)
            val delay = Config.teleportDelay
            warpName = Warps.getWarpName(warpName)

            if (delay > 0) {
                sender.sendTl("commencingTeleport", warpName, delay)
                sender.sendTl("doNotMove")
            }
            Delay(player, delay) {
                try {
                    charge(player)
                    player.teleport(location)
                    sender.sendTl("teleportedToWarp", warpName)
                } catch (e: NoFundsException) {
                    for (message in e.messages) {
                        player.sendMessage(message)
                    }
                }
            }
        }
    }
}