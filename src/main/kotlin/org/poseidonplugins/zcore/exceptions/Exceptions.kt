package org.poseidonplugins.zcore.exceptions

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.formatError
import java.util.UUID

open class CommandException(vararg val messages: String) : RuntimeException()

class InvalidUsageException(command: Command) : CommandException(command.description, "Usage: ${command.usage}")

class PlayerNotFoundException(val name: String) : CommandException(formatError("playerNotFound", "name" to name))

class UnknownUserException(val uuid: UUID) : CommandException(formatError("unknownUser", "uuid" to uuid))

class NoFundsException : CommandException(formatError("noFunds"))

class BalanceOutOfBoundsException(val uuid: UUID) : CommandException(
    formatError("balanceOutOfBounds", "user" to PlayerMap.getPlayer(uuid).name,
                "amount" to Economy.formatBalance(Config.getDouble("maxBalance", 0.0, 10000000000000.0)))
)

class UnsafeDestinationException : CommandException(formatError("unsafeDestination"))