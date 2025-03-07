package me.zavdav.zcore.api

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.user.UserMap
import me.zavdav.zcore.util.BalanceOutOfBoundsException
import me.zavdav.zcore.util.NoFundsException
import me.zavdav.zcore.util.UnknownUserException
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * This class provides methods to access ZCore's economy.
 */
object Economy {

    const val MAX_BALANCE: Double = 10000000000000.0
    private val formatter: NumberFormat = NumberFormat.getNumberInstance(Locale.US)

    /**
     * Checks if a player exists.
     *
     * @param uuid the player's UUID
     * @return if the player exists
     */
    @JvmStatic
    fun userExists(uuid: UUID): Boolean = UserMap.isUserKnown(uuid)

    /**
     * Returns a player's balance, rounded to 2 decimal places.
     *
     * @param uuid the player's UUID
     * @return the player's balance
     * @throws [UnknownUserException] if the player does not exist
     */
    @JvmStatic
    fun getBalance(uuid: UUID): Double {
        if (!userExists(uuid)) throw UnknownUserException(uuid)
        return User.from(uuid).balance
    }

    /**
     * Sets a player's balance, rounded to 2 decimal places.
     *
     * @param uuid the player's UUID
     * @param amount the new balance
     * @return the new balance
     * @throws [UnknownUserException] if the player does not exist
     * @throws [BalanceOutOfBoundsException] if the new balance would be higher than the maximum balance
     */
    @JvmStatic
    fun setBalance(uuid: UUID, amount: Double): Double {
        if (!userExists(uuid)) throw UnknownUserException(uuid)
        if (isOutOfBounds(amount)) throw BalanceOutOfBoundsException(uuid)
        User.from(uuid).balance = amount.roundTo2()
        return getBalance(uuid)
    }

    /**
     * Adds an amount to a player's balance, rounded to 2 decimal places.
     *
     * @param uuid the player's UUID
     * @param amount the amount to add to the player's balance
     * @throws [UnknownUserException] if the player does not exist
     * @throws [BalanceOutOfBoundsException] if the new balance would be higher than the maximum balance
     */
    @JvmStatic
    fun addBalance(uuid: UUID, amount: Double) {
        setBalance(uuid, getBalance(uuid) + amount.roundTo2())
    }

    /**
     * Subtracts an amount from a player's balance, rounded to 2 decimal places.
     *
     * @param uuid the player's UUID
     * @param amount the amount to subtract from the player's balance
     * @throws [UnknownUserException] if the player does not exist
     * @throws [NoFundsException] if the new balance would be below zero
     * @throws [BalanceOutOfBoundsException] if the new balance would be higher than the maximum balance
     */
    @JvmStatic
    fun subtractBalance(uuid: UUID, amount: Double) {
        if (!hasEnough(uuid, amount)) throw NoFundsException()
        setBalance(uuid, getBalance(uuid) - amount.roundTo2())
    }

    /**
     * Multiplies a player's balance with a factor, rounded to 2 decimal places.
     *
     * @param uuid the player's UUID
     * @param amount the factor to multiply with
     * @throws [UnknownUserException] if the player does not exist
     * @throws [BalanceOutOfBoundsException] if the new balance would be higher than the maximum balance
     */
    @JvmStatic
    fun multiplyBalance(uuid: UUID, amount: Double) {
        setBalance(uuid, getBalance(uuid) * amount)
    }

    /**
     * Divides a player's balance by a factor, rounded to 2 decimal places.
     *
     * @param uuid the player's UUID
     * @param amount the factor to divide by
     * @throws [UnknownUserException] if the player does not exist
     * @throws [BalanceOutOfBoundsException] if the new balance would be higher than the maximum balance
     */
    @JvmStatic
    fun divideBalance(uuid: UUID, amount: Double) {
        setBalance(uuid, getBalance(uuid) / amount)
    }

    /**
     * Transfers an amount from one player to another player, rounded to 2 decimal places.
     *
     * @param sender the sender's UUID
     * @param receiver the receiver's UUID
     * @param amount the amount to transfer
     * @throws [UnknownUserException] if any of the players do not exist
     * @throws [NoFundsException] if the sender's new balance would be below zero
     * @throws [BalanceOutOfBoundsException] if the receiver's new balance would be higher than the maximum balance
     */
    @JvmStatic
    fun transferBalance(sender: UUID, receiver: UUID, amount: Double) {
        if (isOutOfBounds(getBalance(receiver) + amount.roundTo2())) {
            throw BalanceOutOfBoundsException(receiver)
        }
        subtractBalance(sender, amount)
        addBalance(receiver, amount)
    }

    /**
     * Checks if a player has at least the amount in their account.
     *
     * @param uuid the player's UUID
     * @param amount the amount to check for
     * @return if the player has at least this amount in their account
     * @throws [UnknownUserException] if the player does not exist
     */
    @JvmStatic
    fun hasEnough(uuid: UUID, amount: Double): Boolean =
        getBalance(uuid) >= amount.roundTo2()

    /**
     * Checks if a player's balance is greater than an amount.
     *
     * @param uuid the player's UUID
     * @param amount the amount
     * @return if the balance is greater than the amount
     * @throws [UnknownUserException] if the player does not exist
     */
    @JvmStatic
    fun hasOver(uuid: UUID, amount: Double): Boolean =
        getBalance(uuid) > amount.roundTo2()

    /**
     * Checks if a player's balance is below an amount.
     *
     * @param uuid the player's UUID
     * @param amount the amount
     * @return if the balance is below the amount
     * @throws [UnknownUserException] if the player does not exist
     */
    @JvmStatic
    fun hasUnder(uuid: UUID, amount: Double): Boolean =
        !hasEnough(uuid, amount)

    /**
     * Checks if an amount is higher than the maximum balance.
     *
     * @param amount the amount to check
     * @return if the amount is out of bounds
     */
    @JvmStatic
    fun isOutOfBounds(amount: Double): Boolean = amount.roundTo2() > Config.maxBalance

    /**
     * Formats an amount to the currency, rounded to 2 decimal places.
     *
     * @param amount the amount to format
     * @return the formatted currency
     */
    @JvmStatic
    fun formatBalance(amount: Double): String {
        val string = "${Config.currency}${formatter.format(amount)}"
        return if (string.endsWith(".00")) string.substring(0, string.length - 3) else string
    }

    /**
     * Rounds this value to 2 decimal places.
     *
     * @return the rounded value
     */
    @JvmStatic
    fun Double.roundTo2(): Double {
        return (this * 10.0.pow(2)).roundToLong() / 10.0.pow(2)
    }
}