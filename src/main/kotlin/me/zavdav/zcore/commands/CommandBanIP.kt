package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.IPV4_PATTERN
import me.zavdav.zcore.util.TIME_PATTERN
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.getPlayerFromString
import me.zavdav.zcore.util.joinArgs
import me.zavdav.zcore.util.parseDuration
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.regex.Pattern

class CommandBanIP : AbstractCommand(
    "banip",
    "Bans an IP address from the server.",
    "/banip <player|ip> [duration] [reason]",
    "zcore.banip",
    false,
    1,
    aliases = listOf("ipban")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val ip =
            if (IPV4_PATTERN.matcher(args[0]).matches()) {
                args[0]
            } else {
                val player = getPlayerFromString(args[0])
                player.address.address.hostAddress
            }

        assert(sender !is Player || sender.address.address.hostAddress != ip, "cannotBanSelf")
        val subArgs = joinArgs(args , 1, args.size)
        val matcher = Pattern.compile("^${TIME_PATTERN.pattern()}").matcher(subArgs)
        val sb = StringBuilder()
        var end = 0

        while (matcher.find() && matcher.start() == end) {
            sb.append(subArgs, matcher.start(), matcher.end())
            end = matcher.end()
        }

        val duration = if (sb.toString().isNotEmpty()) parseDuration(sb.toString()) else null
        val reason = subArgs.substring(end).trim().takeIf { it.isNotEmpty() } ?: tl("banReason")
        Punishments.banIP(ip, (sender as? Player)?.uniqueId, duration, reason)

        if (duration == null) {
            sender.sendTl("bannedIp", "ip" to ip, "reason" to reason)
        } else {
            sender.sendTl("tempBannedIp",
                "ip" to ip,
                "duration" to formatDuration(duration * 1000),
                "reason" to reason)
        }
    }
}