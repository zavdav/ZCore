package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.sendErrTl
import org.poseidonplugins.zcore.util.sendTl

class CommandIgnoreList : Command(
    "ignorelist",
    listOf("ignores"),
    "Shows a list of your ignored players.",
    "/ignorelist",
    "zcore.ignorelist",
    true,
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val ignores = PlayerMap.getPlayer(event.sender as Player).ignores
        if (ignores.isEmpty()) {
            event.sender.sendErrTl("noIgnoredPlayers")
            return
        }

        event.sender.sendTl("ignoreList",
            "list" to ignores.joinToString(", ") { PlayerMap.getPlayer(it).name })
    }
}