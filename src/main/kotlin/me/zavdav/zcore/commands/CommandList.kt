package me.zavdav.zcore.commands

import me.zavdav.zcore.util.sendTl
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandList : ZCoreCommand(
    "list",
    listOf("online", "who", "playerlist"),
    "Lists all online players.",
    "/list",
    "zcore.list",
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        event.sender.sendTl("listPlayers",
            "amount" to Bukkit.getOnlinePlayers().size,
            "max" to Bukkit.getMaxPlayers())
        event.sender.sendMessage(Bukkit.getOnlinePlayers()
            .map { p: Player -> p.displayName }.sorted().joinToString(", "))
    }
}