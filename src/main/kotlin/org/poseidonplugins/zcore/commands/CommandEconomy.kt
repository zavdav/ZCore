package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.exceptions.InvalidUsageException
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.*
import org.poseidonplugins.zcore.util.Utils.roundTo

class CommandEconomy : Command(
    "economy",
    listOf("eco"),
    "Manages the economy.",
    "/economy <set/give/take> <player/uuid> <amount>",
    "zcore.economy",
    true,
    3,
    3,
    Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val uuid = Utils.getUUIDFromString(event.args[1])
        val name = PlayerMap.getPlayer(uuid).name
        var amount = event.args[2].toDoubleOrNull()?.roundTo(2)

        if (amount == null || amount < 0) {
            event.sender.sendErrTl("invalidAmount")
            return
        }
        when (event.args[0].lowercase()) {
            "set" -> {
                Economy.setBalance(uuid, amount)
                event.sender.sendTl("setBalance", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            "give" -> {
                Economy.addBalance(uuid, amount)
                event.sender.sendTl("gaveMoney", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            "take" -> {
                if (!Economy.hasEnough(uuid, amount)) amount = Economy.getBalance(uuid)
                Economy.subtractBalance(uuid, amount)
                event.sender.sendTl("tookMoney", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            else -> throw InvalidUsageException()
        }
    }
}