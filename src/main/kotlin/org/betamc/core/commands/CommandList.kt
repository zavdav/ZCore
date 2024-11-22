package org.betamc.core.commands

import org.betamc.core.config.Language
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage

class CommandList : Command(
    "list",
    listOf("online, who, playerlist"),
    "Lists all online players.",
    "/list",
    "bmc.list",
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        sendMessage(event.sender, Language.LIST_HEADER.msg
            .replace("%count%", "${Bukkit.getOnlinePlayers().size}")
            .replace("%max%", "${Bukkit.getMaxPlayers()}"))
        sendMessage(event.sender, Language.LIST_PLAYERS.msg
            .replace("%list%", Bukkit.getOnlinePlayers().map { p: Player -> p.name }.sorted().joinToString(", ")))
    }
}