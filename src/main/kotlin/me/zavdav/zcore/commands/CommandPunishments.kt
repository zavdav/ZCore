package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.data.Ban
import me.zavdav.zcore.data.IPBan
import me.zavdav.zcore.data.Mute
import me.zavdav.zcore.data.Punishment
import me.zavdav.zcore.util.IPV4_PATTERN
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.formatEpoch
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.getUsernameFromUUID
import me.zavdav.zcore.util.send
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.poseidonplugins.commandapi.CommandEvent
import kotlin.math.ceil

class CommandPunishments : ZCoreCommand(
    "punishments",
    listOf("phistory"),
    "Lists a player's punishments.",
    "/punishments <player|ip> [page]",
    "zcore.punishments",
    minArgs = 1,
    maxArgs = 2
) {

    override fun execute(event: CommandEvent) {
        val name: String
        val punishments: List<Punishment>
        if (IPV4_PATTERN.matcher(event.args[0]).matches()) {
            name = event.args[0]
            punishments = Punishments.getIPBans(name)
        } else {
            val uuid = getUUIDFromString(event.args[0])
            name = getUsernameFromUUID(uuid) ?: uuid.toString()
            punishments = (Punishments.getMutes(uuid) + Punishments.getBans(uuid)).sortedBy { it.timeIssued }
        }

        assert(punishments.isNotEmpty(), "noPunishments", "name" to name)
        var page = 1
        if (event.args.size == 2) {
            page = (event.args[1].toIntOrNull() ?: 1).coerceAtLeast(1)
        }
        printPunishments(event.sender, page, name, punishments)
    }

    private fun printPunishments(sender: CommandSender, page: Int, name: String, punishments: List<Punishment>) {
        val pages = ceil(punishments.size.toDouble() / 2).toInt()
        assert(page <= pages, "pageTooHigh", "page" to page)
        sender.sendTl("punishmentsPage", "name" to name, "page" to page, "pages" to pages)

        for (i in (page * 2 - 2)..<page * 2) {
            if (i >= punishments.size) break
            val it = punishments[i]
            val type = when (it) {
                is Mute -> "Mute"
                is Ban -> "Ban"
                is IPBan -> "IP Ban"
                else -> "Unknown"
            }

            sender.sendTl("punishmentType", "type" to type)
            sender.sendTl("punishmentIssuer",
                "name" to (it.issuer?.let { getUsernameFromUUID(it) } ?: "Console"))
            sender.sendTl("punishmentIssuedTime", "datetime" to formatEpoch(it.timeIssued))
            sender.sendTl("punishmentDuration",
                "duration" to (it.duration?.let { formatDuration(it * 1000) } ?: "Permanent"))
            sender.sendTl("punishmentReason", "reason" to it.reason)
            sender.sendTl("punishmentPardoned", "status" to it.pardoned)
            sender.send("--------------------")
        }
    }
}