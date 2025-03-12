package me.zavdav.zcore.api

import me.zavdav.zcore.util.tl
import java.util.UUID

abstract class EconomyException(val uuid: UUID, message: String) : Exception(message)

class UnknownUserException(uuid: UUID) : EconomyException(uuid, tl("unknownUser", uuid))

class LoanNotPermittedException(uuid: UUID) : EconomyException(uuid, tl("noFunds"))