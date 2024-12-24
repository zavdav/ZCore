package org.poseidonplugins.zcore.commands

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.util.format

class CommandList : Command(
    "list",
    listOf("online, who, playerlist"),
    "Lists all online players.",
    "/list",
    "zcore.list",
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        sendMessage(event.sender, format("listPlayers",
            "amount" to Bukkit.getOnlinePlayers().size,
            "max" to Bukkit.getMaxPlayers()))
        sendMessage(event.sender, Bukkit.getOnlinePlayers()
            .map { p: Player -> p.name }.sorted().joinToString(", "))
    }
}