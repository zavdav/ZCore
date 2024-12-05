package org.betamc.core.commands

import org.betamc.core.config.Language
import org.bukkit.command.CommandSender
import org.poseidonplugins.commandapi.*
import kotlin.math.ceil

class CommandHelp : Command(
    "help",
    description = "Shows a list of available commands.",
    usage = "/help [page/query]",
    permission = "bmc.help",
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        var query = event.args.joinToString(" ")
        var page = if (query.isEmpty()) 1 else event.args[0].toIntOrNull()
        var commands = getPluginCommands().map { entry -> entry.value }
            .filter { command ->
                if (!event.sender.isOp && (command.permission == null || command.permission == "")) {
                    command.plugin.description.name == "BMC-Core" && hasPermission(event.sender, "bmc.${command.name}")
                }
                else hasPermission(event.sender, command.permission)
            }

        if (page == null) {
            page = event.args[event.args.size-1].toIntOrNull()
            if (page != null) {
                query = event.args.subList(0, event.args.size-1).joinToString(" ")
            }

            commands = commands.filter { command -> command.name.contains(query, true) || command.description.contains(query, true) }
            if (commands.isEmpty()) {
                sendMessage(event.sender, Language.NO_MATCHING_RESULTS)
                return
            }
        }
        if (page == null || page <= 0) page = 1
        printHelp(event.sender, page, commands)
    }

    private fun printHelp(sender: CommandSender, page: Int, commands: List<org.bukkit.command.Command>) {
        val pages = ceil(commands.size.toDouble() / 10).toInt()
        if (page > pages) {
            sendMessage(sender, Language.PAGE_TOO_HIGH)
            return
        }

        sendMessage(sender, Language.HELP_HEADER.msg
            .replace("%page%", "$page")
            .replace("%pages%", "$pages"))

        for (i in (page * 10 - 10)..<page * 10) {
            if (i >= commands.size) break
            sendMessage(sender, Language.HELP_COMMAND.msg
                .replace("%command%", commands[i].name).replace("%description%", commands[i].description))
        }
    }
}