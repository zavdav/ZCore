package org.poseidonplugins.zcore.exceptions

import java.util.UUID

class PlayerNotFoundException(val username: String) : Exception()

class UnknownUserException(val uuid: UUID) : Exception()

class NoFundsException : Exception()

class BalanceOutOfBoundsException(val uuid: UUID) : Exception()

class UnsafeDestinationException : Exception()