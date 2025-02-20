package org.poseidonplugins.zcore.util

import org.bukkit.command.CommandSender
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.user.User
import java.util.UUID

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