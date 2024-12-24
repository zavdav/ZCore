package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError

class CommandDelHome : Command(
    "delhome",
    listOf("dh"),
    "Deletes the specified home.",
    "/delhome <name>",
    "zcore.delhome",
    true,
    1,
    1,
    Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var zPlayer = PlayerMap.getPlayer(player)
        var homeName = event.args[0]

        if (homeName.contains(":")) {
            if (!hasPermission(event.sender, "zcore.delhome.others")) {
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
            zPlayer = PlayerMap.getPlayer(uuid)
            homeName = strings[1]
        }

        if (!zPlayer.homeExists(homeName)) {
            sendMessage(event.sender, formatError("homeDoesNotExist"))
            return
        }

        val finalName = zPlayer.getFinalHomeName(homeName)
        zPlayer.removeHome(finalName)

        if (player.uniqueId == zPlayer.uuid) {
            sendMessage(event.sender, format("homeDeleted",
                "home" to finalName))
        } else {
            sendMessage(event.sender, format("homeDeletedOther",
                "user" to zPlayer.name,
                "home" to finalName))
        }
    }
}