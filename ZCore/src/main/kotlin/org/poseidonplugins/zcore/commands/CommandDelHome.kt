package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.*

class CommandDelHome : ZCoreCommand(
    "delhome",
    listOf("dh"),
    "Deletes the specified home.",
    "/delhome <name>",
    "zcore.delhome",
    true,
    1,
    1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var user = User.from(player)
        var homeName = event.args[0]

        if (":" in homeName) {
            assert(hasPermission(event.sender, "zcore.delhome.others"), "noPermission")
            val strings = event.args[0].split(":", limit = 2)
            assert(strings[1].isNotEmpty(), "noHomeSpecified")

            val uuid = Utils.getUUIDFromUsername(strings[0])
            user = User.from(uuid)
            homeName = strings[1]
        }

        assert(user.homeExists(homeName), "homeNotFound")
        homeName = user.getHomeName(homeName)
        user.removeHome(homeName)

        if (player.uniqueId == user.uuid) {
            event.sender.sendTl("homeDeleted", "home" to homeName)
        } else {
            event.sender.sendTl("homeDeletedOther", "user" to user.name, "home" to homeName)
        }
    }
}