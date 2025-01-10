package org.poseidonplugins.zcore.commands

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.util.sendTl

class CommandList : Command(
    "list",
    listOf("online", "who", "playerlist"),
    "Lists all online players.",
    "/list",
    "zcore.list",
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        event.sender.sendTl("listPlayers",
            "amount" to Bukkit.getOnlinePlayers().size,
            "max" to Bukkit.getMaxPlayers())
        event.sender.sendMessage(Bukkit.getOnlinePlayers()
            .map { p: Player -> p.displayName }.sorted().joinToString(", "))
    }
}