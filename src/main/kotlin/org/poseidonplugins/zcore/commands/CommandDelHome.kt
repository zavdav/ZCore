package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.*

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
            assert(hasPermission(event.sender, "zcore.delhome.others"), "noPermission")
            val strings = event.args[0].split(":", limit = 2)
            assert(strings[1].isNotEmpty(), "noHomeSpecified")

            val uuid = Utils.getUUIDFromUsername(strings[0])
            zPlayer = PlayerMap.getPlayer(uuid)
            homeName = strings[1]
        }

        assert(zPlayer.homeExists(homeName), "homeDoesNotExist")
        val finalName = zPlayer.getFinalHomeName(homeName)
        zPlayer.removeHome(finalName)

        if (player.uniqueId == zPlayer.uuid) {
            event.sender.sendTl("homeDeleted", "home" to finalName)
        } else {
            event.sender.sendTl("homeDeletedOther", "user" to zPlayer.name, "home" to finalName)
        }
    }
}