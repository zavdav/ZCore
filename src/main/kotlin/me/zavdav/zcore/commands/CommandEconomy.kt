package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.api.Economy.roundTo2
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.InvalidSyntaxException
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender

class CommandEconomy : AbstractCommand(
    "economy",
    "Modifies a player's balance.",
    "/economy <set|give|take> <player> <amount>",
    "zcore.economy",
    minArgs = 3,
    maxArgs = 3,
    aliases = listOf("eco")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val uuid = getUUIDFromString(args[1])
        val name = User.from(uuid).name
        var amount = args[2].toDoubleOrNull()?.roundTo2()
        assert(amount != null && amount >= 0, "invalidAmount", "string" to amount.toString())

        when (args[0].lowercase()) {
            "set" -> {
                Economy.setBalance(uuid, amount!!)
                sender.sendTl("setBalance", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            "give" -> {
                Economy.addBalance(uuid, amount!!)
                sender.sendTl("gaveMoney", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            "take" -> {
                if (!Economy.hasEnough(uuid, amount!!)) amount = Economy.getBalance(uuid)
                Economy.subtractBalance(uuid, amount)
                sender.sendTl("tookMoney", "user" to name, "amount" to Economy.formatBalance(amount))
            }
            else -> throw InvalidSyntaxException(this)
        }
    }
}