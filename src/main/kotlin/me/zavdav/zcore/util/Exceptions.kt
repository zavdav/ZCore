package me.zavdav.zcore.util

import me.zavdav.zcore.commands.core.AbstractCommand
import org.bukkit.command.CommandSender

open class CommandException(val sender: CommandSender, errorMessage: String)
: RuntimeException(null, null, false, false)
{
    init { sender.sendMessage(errorMessage) }
}

class CommandSyntaxException(sender: CommandSender, command: AbstractCommand)
: CommandException(sender, command.description)
{
    init { sender.sendMessage("Syntax: ${command.syntax}") }
}

class MiscellaneousException(message: String): RuntimeException(message, null, false, false)