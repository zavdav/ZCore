package org.poseidonplugins.zcore.api

import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.exceptions.BalanceOutOfBoundsException
import org.poseidonplugins.zcore.exceptions.NoFundsException
import org.poseidonplugins.zcore.exceptions.UnknownUserException
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import java.util.UUID

object Economy {

    fun userExists(uuid: UUID): Boolean = PlayerMap.isPlayerKnown(uuid)

    fun getBalance(uuid: UUID): Double {
        if (!userExists(uuid)) throw UnknownUserException(uuid)
        return PlayerMap.getPlayer(uuid).balance
    }

    fun setBalance(uuid: UUID, amount: Double): Double {
        if (!userExists(uuid)) throw UnknownUserException(uuid)
        if (isOutOfBounds(amount)) throw BalanceOutOfBoundsException(uuid)
        PlayerMap.getPlayer(uuid).balance = amount
        return getBalance(uuid)
    }

    fun addBalance(uuid: UUID, amount: Double) {
        setBalance(uuid, getBalance(uuid) + amount)
    }

    fun subtractBalance(uuid: UUID, amount: Double) {
        if (!hasEnough(uuid, amount)) throw NoFundsException()
        setBalance(uuid, getBalance(uuid) - amount)
    }

    fun transferBalance(sender: UUID, receiver: UUID, amount: Double) {
        if (isOutOfBounds(getBalance(receiver) + amount))
            throw BalanceOutOfBoundsException(receiver)
        subtractBalance(sender, amount)
        addBalance(receiver, amount)
    }

    fun hasEnough(uuid: UUID, amount: Double): Boolean =
        getBalance(uuid) >= amount

    fun isOutOfBounds(amount: Double) = amount > Config.getDouble("maxBalance", 0.0, 10000000000000.0)

    fun formatBalance(amount: Double): String = Utils.formatBalance(amount)
}