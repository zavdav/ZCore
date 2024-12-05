package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.config.Property
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage
import kotlin.math.ceil

class CommandHomes : Command(
    "homes",
    listOf("hs"),
    "Shows a list of your homes.",
    "/homes [page/query]",
    "bmc.homes",
    true,
    maxArgs = 2,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var bmcPlayer = PlayerMap.getPlayer(player)
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

        if (query.contains(":")) {
            val strings = query.split(":")
            val uuid = Utils.getUUIDFromUsername(strings[0])
            if (uuid == null) {
                sendMessage(event.sender, Language.PLAYER_NOT_FOUND.msg
                    .replace("%player%", strings[0]))
                return
            }
            bmcPlayer = PlayerMap.getPlayer(uuid)
            query = strings.getOrNull(1) ?: ""
        }

        if (player.uniqueId != bmcPlayer.getUUID() && !hasPermission(event.sender, "bmc.homes.others")) {
            sendMessage(event.sender, Language.NO_PERMISSION)
            return
        }

        var homes = bmcPlayer.getHomes().sorted()
        if (page < 1) page = 1
        if (query != "") homes = homes.filter { home -> home.startsWith(query, true) }
        if (homes.isEmpty()) {
            sendMessage(event.sender, Language.NO_MATCHING_RESULTS)
            return
        }

        printHomes(event.sender, page, homes)
    }

    private fun printHomes(sender: CommandSender, page: Int, homes: List<String>) {
        val homesPerPage = Property.HOMES_PER_PAGE.toInt()
        val pages = ceil(homes.size.toDouble() / homesPerPage).toInt()
        if (page > pages) {
            sendMessage(sender, Language.PAGE_TOO_HIGH)
            return
        }
        sendMessage(sender, Language.HOMES_HEADER.msg
            .replace("%page%", "$page")
            .replace("%pages%", "$pages"))

        val sb = StringBuilder()
        for (i in (page * homesPerPage - homesPerPage)..<page * homesPerPage) {
            if (i >= homes.size) break
            sb.append(Language.HOMES_ENTRY.msg.replace("%home%", homes[i]))
        }
        sendMessage(sender, sb.substring(0, sb.length - 2))
    }
}