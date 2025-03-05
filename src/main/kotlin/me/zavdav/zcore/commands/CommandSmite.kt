package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSmite : AbstractCommand(
    "smite",
    "Strikes lightning at your cursor position or at a player.",
    "/smite [player]",
    "zcore.smite",
    maxArgs = 1,
    aliases = listOf("lightning")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player

        if (args.isNotEmpty()) {
            val target = getPlayerFromUsername(args[0])
            val location = target.location
            location.world.strikeLightning(location)
            player.sendTl("struckPlayer", target.name)
        } else {
            val location = player.getTargetBlock(null, 100).location
            location.world.strikeLightning(location)
            player.sendTl("struckLightning")
        }
    }
}