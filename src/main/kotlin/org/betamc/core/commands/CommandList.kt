package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.util.Utils
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
        sendMessage(event.sender, Utils.format(Language.LIST_HEADER,
            Bukkit.getOnlinePlayers().size, Bukkit.getMaxPlayers()))
        sendMessage(event.sender, Bukkit.getOnlinePlayers()
            .map { p: Player -> p.name }.sorted().joinToString(", "))
    }
}