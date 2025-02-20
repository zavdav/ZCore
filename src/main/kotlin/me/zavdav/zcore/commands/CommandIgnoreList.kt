package me.zavdav.zcore.commands

import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

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