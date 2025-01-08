package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
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

        if (":" in homeName) {
            if (!hasPermission(event.sender, "zcore.delhome.others")) {
                event.sender.sendMessage(format("noPermission"))
                return
            }

            val strings = event.args[0].split(":")
            if (strings.size < 2) {
                event.sender.sendMessage(formatError("noHomeSpecified"))
                return
            }

            val uuid = Utils.getUUIDFromUsername(strings[0])
            zPlayer = PlayerMap.getPlayer(uuid)
            homeName = strings[1]
        }

        if (!zPlayer.homeExists(homeName)) {
            event.sender.sendMessage(formatError("homeDoesNotExist"))
            return
        }

        val finalName = zPlayer.getFinalHomeName(homeName)
        zPlayer.removeHome(finalName)

        if (player.uniqueId == zPlayer.uuid) {
            event.sender.sendMessage(format("homeDeleted",
                "home" to finalName))
        } else {
            event.sender.sendMessage(format("homeDeletedOther",
                "user" to zPlayer.name,
                "home" to finalName))
        }
    }
}