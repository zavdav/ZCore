package org.poseidonplugins.zcore.exceptions

class PlayerNotFoundException(val username: String) : Exception()

class UnsafeDestinationException : Exception()