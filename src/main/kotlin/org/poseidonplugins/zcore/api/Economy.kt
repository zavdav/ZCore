package org.poseidonplugins.zcore.api

import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.BalanceOutOfBoundsException
import org.poseidonplugins.zcore.util.NoFundsException
import org.poseidonplugins.zcore.util.UnknownUserException
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.roundTo
import java.util.UUID

object Economy {

    const val MAX_BALANCE: Double = 10000000000000.0

    fun userExists(uuid: UUID): Boolean = PlayerMap.isPlayerKnown(uuid)

    fun getBalance(uuid: UUID): Double {
        if (!userExists(uuid)) throw UnknownUserException(uuid)
        return PlayerMap.getPlayer(uuid).balance
    }

    fun setBalance(uuid: UUID, amount: Double): Double {
        if (!userExists(uuid)) throw UnknownUserException(uuid)
        if (isOutOfBounds(amount)) throw BalanceOutOfBoundsException(uuid)
        PlayerMap.getPlayer(uuid).balance = amount.roundTo(2)
        return getBalance(uuid)
    }

    fun addBalance(uuid: UUID, amount: Double) {
        setBalance(uuid, getBalance(uuid) + amount.roundTo(2))
    }

    fun subtractBalance(uuid: UUID, amount: Double) {
        if (!hasEnough(uuid, amount)) throw NoFundsException()
        setBalance(uuid, getBalance(uuid) - amount.roundTo(2))
    }

    fun transferBalance(sender: UUID, receiver: UUID, amount: Double) {
        if (isOutOfBounds(getBalance(receiver) + amount.roundTo(2))) {
            throw BalanceOutOfBoundsException(receiver)
        }
        subtractBalance(sender, amount)
        addBalance(receiver, amount)
    }

    fun hasEnough(uuid: UUID, amount: Double): Boolean =
        getBalance(uuid) >= amount.roundTo(2)

    fun isOutOfBounds(amount: Double) = amount.roundTo(2) > Config.getDouble("maxBalance", 0.0, MAX_BALANCE)

    fun formatBalance(amount: Double): String = Utils.formatBalance(amount)
}