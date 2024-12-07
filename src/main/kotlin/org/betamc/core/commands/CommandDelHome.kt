package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
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
                sendMessage(event.sender, Language.NO_PERMISSION)
                return
            }

            val strings = event.args[0].split(":")
            if (strings.size < 2) {
                sendMessage(event.sender, Language.HOME_NOT_SPECIFIED)
                return
            }

            val uuid = Utils.getUUIDFromUsername(strings[0])
            if (uuid == null) {
                sendMessage(event.sender, Utils.format(Language.PLAYER_NOT_FOUND, strings[0]))
                return
            }
            bmcPlayer = PlayerMap.getPlayer(uuid)
            homeName = strings[1]
        }

        if (!bmcPlayer.homeExists(homeName)) {
            sendMessage(event.sender, Language.HOME_DOES_NOT_EXIST)
            return
        }

        val finalName = bmcPlayer.getFinalHomeName(homeName)
        bmcPlayer.removeHome(finalName)
        sendMessage(event.sender, Utils.format(Language.DELHOME_SUCCESS,
            if (player.uniqueId == bmcPlayer.getUUID()) "Your" else "${bmcPlayer.getName()}'s", finalName))
    }
}