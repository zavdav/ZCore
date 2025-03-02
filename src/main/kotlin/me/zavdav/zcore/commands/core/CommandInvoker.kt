package me.zavdav.zcore.commands.core

import me.zavdav.zcore.util.CommandException
import me.zavdav.zcore.util.InvalidSyntaxException
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.isAuthorized
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CommandInvoker {

    tailrec fun invokeCommand(command: AbstractCommand, sender: CommandSender, args: List<String>) {
        val subcommand = if (args.isNotEmpty()) command.getSubcommand(args[0]) else null

        if (subcommand != null) {
            val newArgs = args.subList(1, args.size)
            invokeCommand(subcommand, sender, newArgs)
        } else runCatching {
            assert(sender.isAuthorized(command.permission), "noPermission")
            assert(sender is Player || !command.playerOnly, "playerOnly")
            assert(args.size >= command.minArgs &&
                  (args.size <= command.maxArgs || command.maxArgs < 0), InvalidSyntaxException(command))

            command.execute(sender, args)
        }.onFailure {
            if (it is CommandException) {
                it.messages.forEach { sender.sendMessage(it) }
            }
            else throw it
        }
    }
}