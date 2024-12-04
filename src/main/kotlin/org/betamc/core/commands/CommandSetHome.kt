package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.config.Property
import org.betamc.core.player.PlayerMap
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage

class CommandSetHome : Command(
    "sethome",
    listOf("sh"),
    "Sets a home at your current location.",
    "/sethome <name>",
    "bmc.sethome",
    true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        var homeName = "main"
        if (event.args.isNotEmpty()) {
            if (!event.args[0].matches("^[a-zA-Z0-9_-]+$".toRegex())) {
                sendMessage(event.sender, Language.SETHOME_INVALID_NAME)
                return
            }
            homeName = event.args[0]
        }

        val bmcPlayer = PlayerMap.getPlayer(event.sender as Player)
        val limit = Property.MULTIPLE_HOMES.toInt()
        val homeCount = bmcPlayer.getHomes().size

        if (!hasPermission(event.sender, "bmc.sethome.unlimited")) {
            if (!hasPermission(event.sender, "bmc.sethome.multiple")) {
                if (homeCount >= 1) {
                    sendMessage(event.sender, Language.SETHOME_MAXIMUM.msg
                        .replace("%amount%", "${1}"))
                    return
                }
            } else if (homeCount >= limit) {
                sendMessage(event.sender, Language.SETHOME_MAXIMUM.msg
                    .replace("%amount%", "$limit"))
                return
            }
        }

        if (bmcPlayer.homeExists(homeName)) {
            sendMessage(event.sender, Language.SETHOME_HOME_EXISTS)
            return
        }

        bmcPlayer.addHome(homeName, (event.sender as Player).location)
        sendMessage(event.sender, Language.SETHOME_SUCCESS.msg
            .replace("%home%", homeName))
    }
}