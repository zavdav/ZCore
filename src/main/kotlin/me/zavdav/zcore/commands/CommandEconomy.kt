package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.api.Economy.roundTo2
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.InvalidUsageException
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.sendTl
import org.poseidonplugins.commandapi.CommandEvent

class CommandEconomy : ZCoreCommand(
    "economy",
    listOf("eco"),
    "Modifies a player's balance.",
    "/economy <set|give|take> <player> <amount>",
    "zcore.economy",
    true,
    3,
    3
) {

    override fun execute(event: CommandEvent) {
        val uuid = getUUIDFromString(event.args[1])
        val name = User.from(uuid).name
        var amount = event.args[2].toDoubleOrNull()?.roundTo2()
        assert(amount != null && amount >= 0, "invalidAmount", "string" to amount.toString())

        when (event.args[0].lowercase()) {
            "set" -> {
                Economy.setBalance(uuid, amount!!)
                event.sender.sendTl("setBalance", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            "give" -> {
                Economy.addBalance(uuid, amount!!)
                event.sender.sendTl("gaveMoney", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            "take" -> {
                if (!Economy.hasEnough(uuid, amount!!)) amount = Economy.getBalance(uuid)
                Economy.subtractBalance(uuid, amount)
                event.sender.sendTl("tookMoney", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            else -> throw InvalidUsageException(this)
        }
    }
}