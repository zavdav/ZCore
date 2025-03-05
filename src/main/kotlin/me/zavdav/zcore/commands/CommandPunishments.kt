package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.commands.core.AbstractCommand
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
import kotlin.math.ceil

class CommandPunishments : AbstractCommand(
    "punishments",
    "Lists a player's punishments.",
    "/punishments <player|ip> [page]",
    "zcore.punishments",
    false,
    1,
    2,
    listOf("phistory")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val name: String
        val punishments: List<Punishment>
        if (IPV4_PATTERN.matcher(args[0]).matches()) {
            name = args[0]
            punishments = Punishments.getIPBans(name)
        } else {
            val uuid = getUUIDFromString(args[0])
            name = getUsernameFromUUID(uuid) ?: uuid.toString()
            punishments = (Punishments.getMutes(uuid) + Punishments.getBans(uuid)).sortedBy { it.timeIssued }
        }

        assert(punishments.isNotEmpty(), "noPunishments", name)
        var page = 1
        if (args.size == 2) {
            page = (args[1].toIntOrNull() ?: 1).coerceAtLeast(1)
        }
        printPunishments(sender, page, punishments)
    }

    private fun printPunishments(sender: CommandSender, page: Int, punishments: List<Punishment>) {
        val pages = ceil(punishments.size.toDouble() / 2).toInt()
        assert(page <= pages, "pageTooHigh", page)
        sender.sendTl("punishmentsPage", page, pages)

        for (i in (page * 2 - 2)..<page * 2) {
            if (i >= punishments.size) break
            val it = punishments[i]
            val type = when (it) {
                is Mute -> "Mute"
                is Ban -> "Ban"
                is IPBan -> "IP Ban"
                else -> "Unknown"
            }

            sender.sendTl("punishmentType", type)
            sender.sendTl("punishmentIssuer", (it.issuer?.let { getUsernameFromUUID(it) } ?: "Console"))
            sender.sendTl("punishmentIssuedTime", formatEpoch(it.timeIssued))
            sender.sendTl("punishmentDuration", (it.duration?.let { formatDuration(it * 1000) } ?: "Permanent"))
            sender.sendTl("punishmentReason", it.reason)
            sender.sendTl("punishmentPardoned", it.pardoned)
            sender.send("--------------------")
        }
    }
}