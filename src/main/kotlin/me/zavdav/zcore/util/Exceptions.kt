package me.zavdav.zcore.util

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import org.bukkit.command.CommandSender
import java.util.UUID

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

open class EconomyException(val uuid: UUID, message: String) : Exception(message, null, false, false)

class UnknownUserException(uuid: UUID) : EconomyException(uuid, tl("unknownUser", uuid))

class NoFundsException(uuid: UUID) : EconomyException(uuid, tl("noFunds"))

class BalanceOutOfBoundsException(uuid: UUID)
: EconomyException(uuid, tl("balanceOutOfBounds", User.from(uuid).name, Economy.formatBalance(Config.maxBalance)))

class MiscellaneousException(message: String): RuntimeException(message, null, false, false)