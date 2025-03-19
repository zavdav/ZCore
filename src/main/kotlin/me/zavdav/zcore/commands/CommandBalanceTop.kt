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
            val users = UserMap.getAllUsers()
                .sortedWith(compareByDescending<User> { it.balance }.thenBy { it.name })

            var page = 1
            if (args.isNotEmpty()) {
                page = (args[0].toIntOrNull() ?: 1).coerceAtLeast(1)
            }

            printBalances(sender, page, user, users)
        }
    }

    private fun printBalances(sender: CommandSender, page: Int, user: User, users: List<User>) {
        val balancesPerPage = Config.balancesPerPage
        val pages = ceil(users.size.toDouble() / balancesPerPage).toInt()
        sender.assertOrSend("pageTooHigh", page) { page <= pages }
        sender.sendTl("balancetopPage", page, pages)

        for (i in (page * balancesPerPage - balancesPerPage)..<page * balancesPerPage) {
            if (i >= users.size) break
            sender.sendTl("balancetopEntry", i + 1,
                users[i].getDisplayName(false),
                Economy.formatBalance(users[i].balance))
        }

        sender.sendTl("balancetopTotal", Economy.formatBalance(users.sumOf { it.balance }))
        sender.sendTl("balancetopRank", Economy.formatBalance(user.balance), users.indexOf(user) + 1)
    }
}