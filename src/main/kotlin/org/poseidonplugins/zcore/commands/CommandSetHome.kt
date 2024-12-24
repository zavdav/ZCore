package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.config.Property
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
                sendMessage(event.sender, formatError("invalidHomeName"))
                return
            }
            homeName = event.args[0]
        }

        val zPlayer = PlayerMap.getPlayer(event.sender as Player)
        val limit = Property.MULTIPLE_HOMES.toUInt()
        val homeCount = zPlayer.getHomes().size

        if (!hasPermission(event.sender, "zcore.sethome.unlimited")) {
            if (!hasPermission(event.sender, "zcore.sethome.multiple")) {
                if (homeCount >= 1) {
                    sendMessage(event.sender, formatError("homeLimitSingle"))
                    return
                }
            } else if (homeCount >= limit) {
                sendMessage(event.sender, formatError("homeLimitMultiple",
                    "amount" to limit))
                return
            }
        }

        if (zPlayer.homeExists(homeName)) {
            sendMessage(event.sender, formatError("homeAlreadyExists"))
            return
        }

        zPlayer.addHome(homeName, (event.sender as Player).location)
        sendMessage(event.sender, format("homeSet", "home" to homeName))
    }
}