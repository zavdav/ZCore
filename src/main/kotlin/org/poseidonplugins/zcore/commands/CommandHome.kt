package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.exceptions.UnsafeDestinationException
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError

class CommandHome : Command(
    "home",
    listOf("h"),
    "Teleports you to your specified home.",
    "/home <name>",
    "zcore.home",
    true,
    1,
    1,
    Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var zPlayer = PlayerMap.getPlayer(player)
        var homeName = event.args[0]

        if (homeName.contains(":")) {
            if (!hasPermission(event.sender, "zcore.home.others")) {
                event.sender.sendMessage(format("noPermission"))
                return
            }

            val strings = event.args[0].split(":", limit = 2)
            val uuid = Utils.getUUIDFromUsername(strings[0])
            if (strings[1].isEmpty()) {
                event.sender.sendMessage(formatError("noHomeSpecified"))
                return
            }

            zPlayer = PlayerMap.getPlayer(uuid)
            homeName = strings[1]
        }

        if (!zPlayer.homeExists(homeName)) {
            event.sender.sendMessage(formatError("homeDoesNotExist"))
            return
        }

        try {
            val location = zPlayer.getHome(homeName)
            player.teleport(location)
        } catch (e: UnsafeDestinationException) {
            event.sender.sendMessage(formatError("unsafeDestination"))
            return
        }

        val finalName = zPlayer.getFinalHomeName(homeName)
        if (player.uniqueId == zPlayer.uuid) {
            event.sender.sendMessage(format("teleportedToHome",
                "home" to finalName))
        } else {
            event.sender.sendMessage(format("teleportedToHomeOther",
                "user" to zPlayer.name,
                "home" to finalName))
        }
    }
}