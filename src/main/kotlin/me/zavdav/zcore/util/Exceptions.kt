package me.zavdav.zcore.util

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import org.bukkit.command.CommandSender
import java.util.UUID

open class CommandException(vararg val messages: String) : RuntimeException()

class AsyncCommandException(val sender: CommandSender, vararg messages: String) : CommandException(*messages)

class InvalidSyntaxException(command: AbstractCommand) : CommandException(command.description, "Syntax: ${command.syntax}")

class PlayerNotFoundException(val name: String) : CommandException(tl("playerNotFound", name))

class UnknownUserException(val uuid: UUID) : CommandException(tl("unknownUser", uuid))

class NoFundsException : CommandException(tl("noFunds"))

class BalanceOutOfBoundsException(val uuid: UUID) : CommandException(
    tl("balanceOutOfBounds", User.from(uuid).name, Economy.formatBalance(Config.maxBalance))
)

class UnsafeDestinationException : CommandException(tl("unsafeDestination"))