package org.betamc.core.commands

import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
import org.betamc.core.util.format
import org.betamc.core.util.formatError
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage

class CommandDelHome : Command(
    "delhome",
    listOf("dh"),
    "Deletes the specified home.",
    "/delhome <name>",
    "bmc.delhome",
    true,
    1,
    1,
    Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var bmcPlayer = PlayerMap.getPlayer(player)
        var homeName = event.args[0]

        if (homeName.contains(":")) {
            if (!hasPermission(event.sender, "bmc.delhome.others")) {
                sendMessage(event.sender, format("noPermission"))
                return
            }

            val strings = event.args[0].split(":")
            if (strings.size < 2) {
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

        val finalName = bmcPlayer.getFinalHomeName(homeName)
        bmcPlayer.removeHome(finalName)

        if (player.uniqueId == bmcPlayer.uuid) {
            sendMessage(event.sender, format("homeDeleted",
                "home" to finalName))
        } else {
            sendMessage(event.sender, format("homeDeletedOther",
                "user" to bmcPlayer.name,
                "home" to finalName))
        }
    }
}