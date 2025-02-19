package org.poseidonplugins.zcore.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.getPluginCommands
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl
import kotlin.math.ceil

class CommandHelp : ZCoreCommand(
    "help",
    description = "Shows a list of available commands.",
    usage = "/help [page|query]",
    permission = "zcore.help"
) {

    override fun execute(event: CommandEvent) {
        var commands = getPluginCommands().values.filter { it.plugin is ZCore }.toMutableList()
        if (Config.listOtherCommands) {
            commands.addAll(getPluginCommands().values.filter { it.plugin !is ZCore })
        }

        commands.removeIf {
            if (it.permission.isNullOrEmpty() && !event.sender.isOp) {
                it.plugin !is ZCore || !hasPermission(event.sender, "zcore.${it.name}")
            }
            else !hasPermission(event.sender, it.permission)
        }

        val page = parseArgs(event.args, commands)
        printHelp(event.sender, page, commands)
    }

    private fun parseArgs(args: List<String>, commands: MutableList<PluginCommand>): Int {
        var page = 1
        if (args.isNotEmpty()) {
            when (args[0].toIntOrNull()) {
                null -> {
                    var query = joinArgs(args, 0)
                    if (args.last().toIntOrNull() != null) {
                        page = args.last().toInt().coerceAtLeast(1)
                        query = joinArgs(args, 0, args.size-1)
                    }

                    commands.removeIf {
                        !it.name.contains(query, true) && !it.description.contains(query, true)
                    }
                    assert(commands.isNotEmpty(), "noMatchingResults")
                }
                else -> page = args[0].toInt().coerceAtLeast(1)
            }
        }
        return page
    }

    private fun printHelp(sender: CommandSender, page: Int, commands: List<Command>) {
        val commandsPerPage = Config.commandsPerPage
        val pages = ceil(commands.size.toDouble() / commandsPerPage).toInt()
        assert(page <= pages, "pageTooHigh")
        sender.sendTl("helpPage", "page" to page, "pages" to pages)

        for (i in (page * commandsPerPage - commandsPerPage)..<page * commandsPerPage) {
            if (i >= commands.size) break
            sender.sendTl("helpEntry", "command" to commands[i].name, "desc" to commands[i].description)
        }
    }
}