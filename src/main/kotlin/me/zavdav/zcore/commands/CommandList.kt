package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.sendTl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class CommandList : AbstractCommand(
    "list",
    "Lists all online players.",
    "/list",
    "zcore.list",
    false,
    maxArgs = 0,
    aliases = listOf("online", "who", "playerlist")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        sender.sendTl("listPlayers",
            "amount" to Bukkit.getOnlinePlayers().size,
            "max" to Bukkit.getMaxPlayers())
        sender.sendMessage(Bukkit.getOnlinePlayers()
            .map { it.displayName }.sorted().joinToString(", "))
    }
}