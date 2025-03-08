package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.user.UserMap
import me.zavdav.zcore.util.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.ceil

class CommandBalanceTop : AbstractCommand(
    "balancetop",
    "Shows a list of the richest players.",
    "/balancetop [page]",
    "zcore.balancetop",
    maxArgs = 1,
    aliases = listOf("baltop")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        asyncDelayedTask {
            val user = User.from(sender as Player)
            val players = UserMap.getAllUsers().toSortedSet(
                compareByDescending<User> { it.balance }.thenBy { it.name }).toList()
            var page = 1
            if (args.isNotEmpty()) {
                page = (args[0].toIntOrNull() ?: 1).coerceAtLeast(1)
            }

            val balancesPerPage = Config.balancesPerPage
            val pages = ceil(players.size.toDouble() / balancesPerPage).toInt()
            sender.assertOrSend("pageTooHigh", page) { page <= pages }
            sender.sendTl("balancetopPage", page, pages)

            for (i in (page * balancesPerPage - balancesPerPage)..<page * balancesPerPage) {
                if (i >= players.size) break
                sender.sendTl("balancetopEntry", i + 1,
                    players[i].getDisplayName(false),
                    Economy.formatBalance(players[i].balance))
            }

            sender.sendTl("balancetopTotal", Economy.formatBalance(players.sumOf { it.balance }))
            sender.sendTl("balancetopRank", Economy.formatBalance(user.balance), players.indexOf(user) + 1)
        }
    }
}