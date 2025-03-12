package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.CommandSyntaxException
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import java.math.BigDecimal

class CommandEconomy : AbstractCommand(
    "economy",
    "Modifies a player's balance.",
    "/economy <set|give|take> <player> <amount>",
    "zcore.economy",
    false,
    3,
    3,
    listOf("eco")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val uuid = getUUIDFromString(args[1])
        val name = User.from(uuid).name
        var amount = args[2].toBigDecimalOrNull()
        sender.assertOrSend ("invalidAmount", args[2]){ amount != null && amount >= BigDecimal.ZERO }

        when (args[0].lowercase()) {
            "set" -> {
                Economy.setBalance(uuid, amount!!)
                sender.sendTl("setBalance", name, Economy.formatBalance(amount))
            }
            "give" -> {
                Economy.addBalance(uuid, amount!!)
                sender.sendTl("gaveMoney", Economy.formatBalance(amount), name)
            }
            "take" -> {
                if (!Economy.hasEnough(uuid, amount!!)) amount = Economy.getBalance(uuid)
                Economy.subtractBalance(uuid, amount)
                sender.sendTl("tookMoney", Economy.formatBalance(amount), name)
            }
            else -> throw CommandSyntaxException(sender, this)
        }
    }
}