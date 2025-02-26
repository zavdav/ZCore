package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission

class CommandBalance : ZCoreCommand(
    "balance",
    listOf("bal"),
    "Shows a player's balance.",
    "/balance [player]",
    "zcore.balance",
    true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var uuid = player.uniqueId
        if (event.args.isNotEmpty()) {
            uuid = getUUIDFromString(event.args[0])
        }

        val isSelf = player.uniqueId == uuid
        assert(isSelf || hasPermission(event.sender, "zcore.balance.others"), "noPermission")
        val amount = Economy.getBalance(uuid)
        val name = User.from(uuid).name

        if (isSelf) {
            event.sender.sendTl("balance", "amount" to Economy.formatBalance(amount))
        } else {
            event.sender.sendTl("balanceOther", "user" to name, "amount" to Economy.formatBalance(amount))
        }
    }
}