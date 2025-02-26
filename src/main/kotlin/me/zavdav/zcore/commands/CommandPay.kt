package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.api.Economy.roundTo2
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandPay : ZCoreCommand(
    "pay",
    description = "Sends money to another player.",
    usage = "/pay <player/uuid> <amount>",
    permission = "zcore.pay",
    isPlayerOnly = true,
    minArgs = 2,
    maxArgs = 2
) {

    override fun execute(event: CommandEvent) {
        val sender = User.from(event.sender as Player)
        val receiver = User.from(getUUIDFromString(event.args[0]))

        val amount = event.args[1].toDoubleOrNull()?.roundTo2()
        assert(amount != null && amount > 0, "invalidAmount", "string" to amount.toString())
        Economy.transferBalance(sender.uuid, receiver.uuid, amount!!)

        event.sender.sendTl("paidMoney",
            "user" to receiver.name, "amount" to Economy.formatBalance(amount))
        if (receiver.isOnline) receiver.player.sendTl("receivedMoney",
            "player" to sender.name, "amount" to Economy.formatBalance(amount))
    }
}