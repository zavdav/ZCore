package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.AsyncCommandException
import org.poseidonplugins.zcore.user.UserMap
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.*
import kotlin.math.ceil

class CommandBalanceTop : ZCoreCommand(
    "balancetop",
    listOf("baltop"),
    "Shows a list of the richest players.",
    "/balancetop [page]",
    "zcore.balancetop",
    true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        asyncDelayedTask {
            val user = User.from(event.sender as Player)
            val players = UserMap.getAllUsers().toSortedSet(
                compareByDescending<User> { it.balance }.thenBy { it.name }).toList()
            var page = 1
            if (event.args.isNotEmpty()) {
                page = (event.args[0].toIntOrNull() ?: 1).coerceAtLeast(1)
            }

            val balancesPerPage = Config.balancesPerPage
            val pages = ceil(players.size.toDouble() / balancesPerPage).toInt()
            assert(page <= pages, AsyncCommandException(event.sender, formatError("pageTooHigh")))
            event.sender.sendTl("balancetopPage", "page" to page, "pages" to pages)

            for (i in (page * balancesPerPage - balancesPerPage)..<page * balancesPerPage) {
                if (i >= players.size) break
                event.sender.sendTl("balancetopEntry",
                    "rank" to i + 1,
                    "name" to players[i].getDisplayName(false),
                    "balance" to Economy.formatBalance(players[i].balance))
            }

            event.sender.sendTl("balancetopTotal",
                "amount" to Economy.formatBalance(players.sumOf { p -> p.balance }))
            event.sender.sendTl("balancetopRank",
                "amount" to Economy.formatBalance(user.balance),
                "rank" to players.indexOf(user) + 1)
        }
    }
}