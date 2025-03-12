package me.zavdav.zcore.api

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.user.UserMap
import me.zavdav.zcore.util.isAuthorized
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.UUID
import kotlin.jvm.Throws

/**
 * This class provides methods to access ZCore's economy.
 * Note that `BigDecimal(String)` is the preferred syntax for creating [BigDecimal]s.
 */
object Economy {

    private val formatter: DecimalFormat = DecimalFormat("#,##0.00")

    /**
     * Checks if a player exists.
     *
     * @param uuid the player's UUID
     * @return if the player exists
     */
    @JvmStatic
    fun userExists(uuid: UUID): Boolean = UserMap.isUserKnown(uuid)

    /**
     * Returns a player's current balance.
     *
     * @param uuid the player's UUID
     * @return the player's balance
     * @throws [UnknownUserException] if the player does not exist
     */
    @Throws(UnknownUserException::class)
    @JvmStatic
    fun getBalance(uuid: UUID): BigDecimal {
        if (!userExists(uuid)) throw UnknownUserException(uuid)
        return User.from(uuid).balance
    }

    /**
     * Sets a player's balance to an amount.
     *
     * @param uuid the player's UUID
     * @param amount the new balance
     * @return the new balance
     * @throws [UnknownUserException] if the player does not exist
     * @throws [LoanNotPermittedException] if the new balance would be negative and the player cannot have a loan
     */
    @Throws(UnknownUserException::class, LoanNotPermittedException::class)
    @JvmStatic
    fun setBalance(uuid: UUID, amount: BigDecimal): BigDecimal {
        if (!userExists(uuid)) throw UnknownUserException(uuid)
        val user = User.from(uuid)

        if (amount < BigDecimal.ZERO) {
            if (user.isOnline) {
                if (!user.player.isAuthorized("zcore.economy.loan"))
                    throw LoanNotPermittedException(uuid)
            } else {
                if (!user.loanPermitted)
                    throw LoanNotPermittedException(uuid)
            }
        }

        user.balance = amount
        return getBalance(uuid)
    }

    /**
     * Adds an amount to a player's balance.
     *
     * @param uuid the player's UUID
     * @param amount the amount to add to the player's balance
     * @throws [UnknownUserException] if the player does not exist
     * @throws [LoanNotPermittedException] if the new balance would be negative and the player cannot have a loan
     */
    @Throws(UnknownUserException::class, LoanNotPermittedException::class)
    @JvmStatic
    fun addBalance(uuid: UUID, amount: BigDecimal) {
        setBalance(uuid, getBalance(uuid) + amount)
    }

    /**
     * Subtracts an amount from a player's balance.
     *
     * @param uuid the player's UUID
     * @param amount the amount to subtract from the player's balance
     * @throws [UnknownUserException] if the player does not exist
     * @throws [LoanNotPermittedException] if the new balance would be negative and the player cannot have a loan
     */
    @Throws(UnknownUserException::class, LoanNotPermittedException::class)
    @JvmStatic
    fun subtractBalance(uuid: UUID, amount: BigDecimal) {
        setBalance(uuid, getBalance(uuid) - amount)
    }

    /**
     * Multiplies a player's balance with an amount.
     *
     * @param uuid the player's UUID
     * @param amount the amount to multiply the player's balance with
     * @throws [UnknownUserException] if the player does not exist
     * @throws [LoanNotPermittedException] if the new balance would be negative and the player cannot have a loan
     */
    @Throws(UnknownUserException::class, LoanNotPermittedException::class)
    @JvmStatic
    fun multiplyBalance(uuid: UUID, amount: BigDecimal) {
        setBalance(uuid, getBalance(uuid) * amount)
    }

    /**
     * Divides a player's balance by an amount.
     *
     * @param uuid the player's UUID
     * @param amount the amount to divide the player's balance by
     * @throws [UnknownUserException] if the player does not exist
     * @throws [LoanNotPermittedException] if the new balance would be negative and the player cannot have a loan
     */
    @Throws(UnknownUserException::class, LoanNotPermittedException::class)
    @JvmStatic
    fun divideBalance(uuid: UUID, amount: BigDecimal) {
        setBalance(uuid, getBalance(uuid) / amount)
    }

    /**
     * Transfers an amount from one player to another player.
     *
     * @param sender the sending player's UUID
     * @param receiver the receiving player's UUID
     * @param amount the amount to transfer
     * @throws [UnknownUserException] if any of the players do not exist
     * @throws [LoanNotPermittedException] if the sender's new balance would be negative and the sender cannot have a loan
     */
    @Throws(UnknownUserException::class, LoanNotPermittedException::class)
    @JvmStatic
    fun transferBalance(sender: UUID, receiver: UUID, amount: BigDecimal) {
        if (!userExists(receiver)) throw UnknownUserException(receiver)
        subtractBalance(sender, amount)
        addBalance(receiver, amount)
    }

    /**
     * Checks if a player's balance is greater than or equal to an amount.
     *
     * @param uuid the player's UUID
     * @param amount the amount
     * @return if the balance is greater than or equal to the amount
     * @throws [UnknownUserException] if the player does not exist
     */
    @Throws(UnknownUserException::class)
    @JvmStatic
    fun hasEnough(uuid: UUID, amount: BigDecimal): Boolean =
        getBalance(uuid) >= amount

    /**
     * Checks if a player's balance is greater than an amount.
     *
     * @param uuid the player's UUID
     * @param amount the amount
     * @return if the balance is greater than the amount
     * @throws [UnknownUserException] if the player does not exist
     */
    @Throws(UnknownUserException::class)
    @JvmStatic
    fun hasOver(uuid: UUID, amount: BigDecimal): Boolean =
        getBalance(uuid) > amount

    /**
     * Checks if a player's balance is below an amount.
     *
     * @param uuid the player's UUID
     * @param amount the amount
     * @return if the balance is below the amount
     * @throws [UnknownUserException] if the player does not exist
     */
    @Throws(UnknownUserException::class)
    @JvmStatic
    fun hasUnder(uuid: UUID, amount: BigDecimal): Boolean =
        getBalance(uuid) < amount

    /**
     * Formats an amount to the currency, rounded down to 2 decimal places.
     *
     * @param amount the amount to format
     * @return the formatted currency
     */
    @JvmStatic
    fun formatBalance(amount: BigDecimal): String {
        var balance = formatter.format(amount.setScale(2, RoundingMode.FLOOR))
        if (balance.endsWith(".00")) balance = balance.substring(0, balance.length - 3)
        return "${Config.currency}$balance"
    }
}