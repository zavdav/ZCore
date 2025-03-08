package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getUUIDFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.ceil

class CommandHomes : AbstractCommand(
    "homes",
    "Shows a list of your homes.",
    "/homes [page|query]",
    "zcore.homes",
    maxArgs = 2,
    aliases = listOf("hl")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var user = User.from(player)
        var query = ""
        var page = 1

        if (args.isNotEmpty()) {
            if (args.size > 1) {
                query = args[0]
                page = args[1].toIntOrNull() ?: 1
            } else {
                try { page = args[0].toInt() }
                catch (_: NumberFormatException) {
                    query = args[0]
                }
            }
        }

        if (":" in query) {
            val strings = query.split(":")
            val uuid = getUUIDFromUsername(strings[0])
            user = User.from(uuid)
            query = strings.getOrNull(1) ?: ""
        }

        sender.assertOrSend("noPermission") {
            player.uniqueId == user.uuid ||it.isAuthorized("zcore.homes.others")
        }
        var homes = user.getHomes().sorted()
        if (page < 1) page = 1
        if (query != "") homes = homes.filter { it.startsWith(query, true) }
        sender.assertOrSend("noMatchingResults") { homes.isNotEmpty() }

        printHomes(sender, page, homes)
    }

    private fun printHomes(sender: CommandSender, page: Int, homes: List<String>) {
        val homesPerPage = Config.homesPerPage
        val pages = ceil(homes.size.toDouble() / homesPerPage).toInt()
        sender.assertOrSend("pageTooHigh", page) { page <= pages }
        sender.sendTl("homesPage", page, pages)

        val sb = StringBuilder()
        for (i in (page * homesPerPage - homesPerPage)..<page * homesPerPage) {
            if (i >= homes.size) break
            sb.append("${homes[i]}, ")
        }
        sender.sendMessage(sb.substring(0, sb.length - 2))
    }
}