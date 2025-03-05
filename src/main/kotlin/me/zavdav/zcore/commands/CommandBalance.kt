package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandBalance : AbstractCommand(
    "balance",
    "Shows a player's balance.",
    "/balance [player]",
    "zcore.balance",
    maxArgs = 1,
    aliases = listOf("bal")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var uuid = player.uniqueId
        if (args.isNotEmpty()) {
            uuid = getUUIDFromString(args[0])
        }

        val isSelf = player.uniqueId == uuid
        assert(isSelf || sender.isAuthorized("zcore.balance.others"), "noPermission")
        val amount = Economy.getBalance(uuid)
        val name = User.from(uuid).name

        if (isSelf) {
            sender.sendTl("balance", Economy.formatBalance(amount))
        } else {
            sender.sendTl("balanceOther", name, Economy.formatBalance(amount))
        }
    }
}