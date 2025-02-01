package org.poseidonplugins.zcore.commands

import org.bukkit.command.CommandSender
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl
import kotlin.math.ceil

class CommandHelp : ZCoreCommand(
    "help",
    description = "Shows a list of available commands.",
    usage = "/help [page/query]",
    permission = "zcore.help"
) {

    override fun execute(event: CommandEvent) {
        var query = event.args.joinToString(" ")
        var page = if (query.isEmpty()) 1 else event.args[0].toIntOrNull()
        var commands = getPluginCommands().map { entry -> entry.value }
            .filter { command ->
                if (!event.sender.isOp && (command.permission == null || command.permission == "")) {
                    command.plugin.description.name == ZCore.plugin.description.name
                    && hasPermission(event.sender, "zcore.${command.name}")
                }
                else hasPermission(event.sender, command.permission)
            }

        if (page == null) {
            page = event.args[event.args.size-1].toIntOrNull()
            if (page != null) {
                query = event.args.subList(0, event.args.size-1).joinToString(" ")
            }

            commands = commands.filter { command -> command.name.contains(query, true) || command.description.contains(query, true) }
            assert(commands.isNotEmpty(), "noMatchingResults")
        }
        if (page == null || page <= 0) page = 1
        printHelp(event.sender, page, commands)
    }

    private fun printHelp(sender: CommandSender, page: Int, commands: List<org.bukkit.command.Command>) {
        val pages = ceil(commands.size.toDouble() / 10).toInt()
        assert(page <= pages, "pageTooHigh")
        sender.sendTl("helpPage", "page" to page, "pages" to pages)

        for (i in (page * 10 - 10)..<page * 10) {
            if (i >= commands.size) break
            sender.sendTl("helpEntry",
                "command" to commands[i].name,
                "description" to commands[i].description)
        }
    }
}