package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.sendTl

class CommandSmite : ZCoreCommand(
    "smite",
    listOf("lightning"),
    "Strikes lightning at your cursor position or at a player.",
    "/smite [player]",
    "zcore.smite",
    true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player

        if (event.args.isNotEmpty()) {
            val target = Utils.getPlayerFromUsername(event.args[0])
            val location = target.location
            location.world.strikeLightning(location)
            player.sendTl("struckPlayer", target)
        } else {
            val location = player.getTargetBlock(null, 100).location
            location.world.strikeLightning(location)
            player.sendTl("struckLightning")
        }
    }
}