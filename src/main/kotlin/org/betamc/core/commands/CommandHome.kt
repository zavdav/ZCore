package org.betamc.core.commands

import org.betamc.core.player.PlayerMap
import org.betamc.core.util.UnsafeDestinationException
import org.betamc.core.util.Utils
import org.betamc.core.util.format
import org.betamc.core.util.formatError
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage

class CommandHome : Command(
    "home",
    listOf("h"),
    "Teleports you to your specified home.",
    "/home <name>",
    "bmc.home",
    true,
    1,
    1,
    Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var bmcPlayer = PlayerMap.getPlayer(player)
        var homeName = event.args[0]

        if (homeName.contains(":")) {
            if (!hasPermission(event.sender, "bmc.home.others")) {
                sendMessage(event.sender, format("noPermission"))
                return
            }

            val strings = event.args[0].split(":", limit = 2)
            if (strings[1].isEmpty()) {
                sendMessage(event.sender, formatError("noHomeSpecified"))
                return
            }

            val uuid = Utils.getUUIDFromUsername(strings[0])
            if (uuid == null) {
                sendMessage(event.sender, formatError("playerNotFound",
                    "player" to strings[0]))
                return
            }
            bmcPlayer = PlayerMap.getPlayer(uuid)
            homeName = strings[1]
        }

        if (!bmcPlayer.homeExists(homeName)) {
            sendMessage(event.sender, formatError("homeDoesNotExist"))
            return
        }

        try {
            val location = bmcPlayer.getHome(homeName)
            player.teleport(location)
        } catch (e: UnsafeDestinationException) {
            sendMessage(event.sender, formatError("unsafeDestination"))
            return
        }

        val finalName = bmcPlayer.getFinalHomeName(homeName)
        if (player.uniqueId == bmcPlayer.uuid) {
            sendMessage(event.sender, format("teleportedToHome",
                "home" to finalName))
        } else {
            sendMessage(event.sender, format("teleportedToHomeOther",
                "user" to bmcPlayer.name,
                "home" to finalName))
        }
    }
}