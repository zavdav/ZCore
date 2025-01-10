package org.poseidonplugins.zcore.commands

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.*
import kotlin.math.ceil

class CommandHomes : Command(
    "homes",
    listOf("hs"),
    "Shows a list of your homes.",
    "/homes [page/query]",
    "zcore.homes",
    true,
    maxArgs = 2,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var zPlayer = PlayerMap.getPlayer(player)
        var query = ""
        var page = 1

        if (event.args.isNotEmpty()) {
            if (event.args.size > 1) {
                query = event.args[0]
                page = event.args[1].toIntOrNull() ?: 1
            } else {
                try { page = event.args[0].toInt() }
                catch (e: NumberFormatException) {
                    query = event.args[0]
                }
            }
        }

        if (":" in query) {
            val strings = query.split(":")
            val uuid = Utils.getUUIDFromUsername(strings[0])
            zPlayer = PlayerMap.getPlayer(uuid)
            query = strings.getOrNull(1) ?: ""
        }

        if (player.uniqueId != zPlayer.uuid && !hasPermission(event.sender, "zcore.homes.others")) {
            event.sender.sendTl("noPermission")
            return
        }

        var homes = zPlayer.getHomes().sorted()
        if (page < 1) page = 1
        if (query != "") homes = homes.filter { home -> home.startsWith(query, true) }
        if (homes.isEmpty()) {
            event.sender.sendErrTl("noMatchingResults")
            return
        }

        printHomes(event.sender, page, homes)
    }

    private fun printHomes(sender: CommandSender, page: Int, homes: List<String>) {
        val homesPerPage = Config.getInt("homesPerPage", 1)
        val pages = ceil(homes.size.toDouble() / homesPerPage).toInt()
        if (page > pages) {
            sender.sendErrTl("pageTooHigh")
            return
        }
        sender.sendTl("homesPage", "page" to page, "pages" to pages)

        val sb = StringBuilder()
        for (i in (page * homesPerPage - homesPerPage)..<page * homesPerPage) {
            if (i >= homes.size) break
            sb.append(format("homesEntry", "home" to homes[i]) + " ")
        }
        sender.sendMessage(sb.substring(0, sb.length - 2))
    }
}