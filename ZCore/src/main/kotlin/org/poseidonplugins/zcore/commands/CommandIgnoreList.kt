package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandIgnoreList : ZCoreCommand(
    "ignorelist",
    listOf("ignores"),
    "Shows a list of your ignored players.",
    "/ignorelist",
    "zcore.ignorelist",
    true,
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        val ignores = User.from(event.sender as Player).ignores
        assert(ignores.isNotEmpty(), "noIgnoredPlayers")

        event.sender.sendTl("ignoreList",
            "list" to ignores.joinToString(", ") { User.from(it).name })
    }
}