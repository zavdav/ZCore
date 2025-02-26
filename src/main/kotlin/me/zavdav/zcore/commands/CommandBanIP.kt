package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.util.IPV4_PATTERN
import me.zavdav.zcore.util.TIME_PATTERN
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.getPlayerFromString
import me.zavdav.zcore.util.parseDuration
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.joinArgs
import java.util.regex.Pattern

class CommandBanIP : ZCoreCommand(
    "banip",
    listOf("ipban"),
    "Bans an IP address from the server.",
    "/banip <player|ip> [duration] [reason]",
    "zcore.banip",
    minArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val ip =
            if (IPV4_PATTERN.matcher(event.args[0]).matches()) {
                event.args[0]
            } else {
                val player = getPlayerFromString(event.args[0])
                player.address.address.hostAddress
            }

        assert(event.sender !is Player || (event.sender as Player).address.address.hostAddress != ip, "cannotBanSelf")
        val subArgs = joinArgs(event.args , 1, event.args.size)
        val matcher = Pattern.compile("^${TIME_PATTERN.pattern()}").matcher(subArgs)
        val sb = StringBuilder()
        var end = 0

        while (matcher.find() && matcher.start() == end) {
            sb.append(subArgs, matcher.start(), matcher.end())
            end = matcher.end()
        }

        val duration = if (sb.toString().isNotEmpty()) parseDuration(sb.toString()) else null
        val reason = subArgs.substring(end).trim().takeIf { it.isNotEmpty() } ?: tl("banReason")
        Punishments.banIP(ip, (event.sender as? Player)?.uniqueId, duration, reason)

        if (duration == null) {
            event.sender.sendTl("bannedIp", "ip" to ip, "reason" to reason)
        } else {
            event.sender.sendTl("tempBannedIp",
                "ip" to ip,
                "duration" to formatDuration(duration * 1000),
                "reason" to reason)
        }
    }
}