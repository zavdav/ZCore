package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.*

class CommandHome : ZCoreCommand(
    "home",
    listOf("h"),
    "Teleports you to the specified home.",
    "/home <name>",
    "zcore.home",
    true,
    1,
    1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var user = User.from(player)
        var homeName = event.args[0]

        if (":" in homeName) {
            assert(hasPermission(event.sender, "zcore.home.others"), "noPermission")
            val strings = event.args[0].split(":", limit = 2)
            assert(strings[1].isNotEmpty(), "noHomeSpecified")

            val uuid = Utils.getUUIDFromUsername(strings[0])
            user = User.from(uuid)
            homeName = strings[1]
        }

        assert(user.homeExists(homeName), "homeNotFound", "home" to homeName)
        val location = user.getHomeLocation(homeName)
        val isSelf = player.uniqueId == user.uuid
        if (isSelf) charge(player)
        player.teleport(location)

        homeName = user.getHomeName(homeName)
        if (isSelf) {
            event.sender.sendTl("teleportedToHome", "home" to homeName)
        } else {
            event.sender.sendTl("teleportedToHomeOther", "user" to user.name, "home" to homeName)
        }
    }
}