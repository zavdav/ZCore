package me.zavdav.zcore.util

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import org.bukkit.command.CommandSender
import org.poseidonplugins.commandapi.Command
import java.util.*

open class CommandException(vararg val messages: String) : RuntimeException()

class AsyncCommandException(val sender: CommandSender, vararg messages: String) : CommandException(*messages)

class InvalidUsageException(command: Command) : CommandException(command.description, "Usage: ${command.usage}")

class PlayerNotFoundException(val name: String) : CommandException(tlError("playerNotFound", "name" to name))

class UnknownUserException(val uuid: UUID) : CommandException(tlError("unknownUser", "user" to uuid))

class NoFundsException : CommandException(tlError("noFunds"))

class BalanceOutOfBoundsException(val uuid: UUID) : CommandException(
    tlError("balanceOutOfBounds",
        "user" to User.from(uuid).name,
        "amount" to Economy.formatBalance(Config.maxBalance)
    )
)

class UnsafeDestinationException : CommandException(tlError("unsafeDestination"))