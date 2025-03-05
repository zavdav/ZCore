package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.commands.core.CommandManager
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.joinArgs
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import kotlin.math.ceil

class CommandHelp : AbstractCommand(
    "help",
    "Shows a list of available commands.",
    "/help [page|query]",
    "zcore.help",
    false
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val commands = CommandManager.zcoreCommands
            .filter { sender.isAuthorized("zcore.${it.name.replace(" ".toRegex(), ".")}") }
            .toMutableList()

        if (Config.listOtherCommands) {
            commands.addAll(CommandManager.otherCommands
                .filter {
                    if (it.permission.isEmpty()) sender.isOp
                    else sender.isAuthorized(it.permission)
                })
        }

        val page = parseArgs(args, commands)
        printHelp(sender, page, commands)
    }

    private fun parseArgs(args: List<String>, commands: MutableList<AbstractCommand>): Int {
        var page = 1
        if (args.isEmpty()) return page

        if (args[0].toIntOrNull() == null) {
            var query = joinArgs(args, 0)
            if (args.last().toIntOrNull() != null) {
                page = args.last().toInt().coerceAtLeast(1)
                query = joinArgs(args, 0, args.lastIndex)
            }

            commands.removeIf {
                !it.name.contains(query, true) && !it.description.contains(query, true)
            }
            assert(commands.isNotEmpty(), "noMatchingResults")
        } else {
            page = args[0].toInt().coerceAtLeast(1)
        }

        return page
    }

    private fun printHelp(sender: CommandSender, page: Int, commands: List<AbstractCommand>) {
        val commandsPerPage = Config.commandsPerPage
        val pages = ceil(commands.size.toDouble() / commandsPerPage).toInt()
        assert(page <= pages, "pageTooHigh", page)
        sender.sendTl("helpPage", page, pages)

        for (i in (page * commandsPerPage - commandsPerPage)..<page * commandsPerPage) {
            if (i >= commands.size) break
            sender.sendTl("helpEntry", commands[i].name, commands[i].description)
        }
    }
}