package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError

class CommandSetHome : Command(
    "sethome",
    listOf("sh"),
    "Sets a home at your current location.",
    "/sethome <name>",
    "zcore.sethome",
    true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        var homeName = "main"
        if (event.args.isNotEmpty()) {
            if (!event.args[0].matches("^[a-zA-Z0-9_-]+$".toRegex())) {
                event.sender.sendMessage(formatError("invalidHomeName"))
                return
            }
            homeName = event.args[0]
        }

        val zPlayer = PlayerMap.getPlayer(event.sender as Player)
        val limit = Config.getInt("multipleHomes", 2)
        val homeCount = zPlayer.getHomes().size

        if (!hasPermission(event.sender, "zcore.sethome.unlimited")) {
            if (!hasPermission(event.sender, "zcore.sethome.multiple")) {
                if (homeCount >= 1) {
                    event.sender.sendMessage(formatError("homeLimitSingle"))
                    return
                }
            } else if (homeCount >= limit) {
                event.sender.sendMessage(formatError("homeLimitMultiple",
                    "amount" to limit))
                return
            }
        }

        if (zPlayer.homeExists(homeName)) {
            event.sender.sendMessage(formatError("homeAlreadyExists"))
            return
        }

        zPlayer.addHome(homeName, (event.sender as Player).location)
        event.sender.sendMessage(format("homeSet", "home" to homeName))
    }
}