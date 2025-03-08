package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.user.UserMap
import me.zavdav.zcore.util.TIME_PATTERN
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.joinArgs
import me.zavdav.zcore.util.parseDuration
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.regex.Pattern

class CommandBan: AbstractCommand(
    "ban",
    "Bans a player from the server.",
    "/ban <player> [duration] [reason]",
    "zcore.ban",
    false,
    1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val uuid = getUUIDFromString(args[0])
        var name = uuid.toString()

        if (UserMap.isUserKnown(uuid)) {
            sender.assertOrSend("cannotBanSelf") { it !is Player || it.uniqueId != uuid }
            val user = User.from(uuid)
            name = user.name
            if (user.isOnline) {
                sender.assertOrSend("cannotBanUser", name) { !user.player.isAuthorized("zcore.ban.exempt") }
            } else {
                sender.assertOrSend("cannotBanUser", name) { !user.banExempt }
            }
        }

        val subArgs = joinArgs(args, 1, args.size)
        val matcher = Pattern.compile("^${TIME_PATTERN.pattern()}").matcher(subArgs)
        val sb = StringBuilder()
        var end = 0

        while (matcher.find() && matcher.start() == end) {
            sb.append(subArgs, matcher.start(), matcher.end())
            end = matcher.end()
        }

        val duration = if (sb.toString().isNotEmpty()) parseDuration(sb.toString()) else null
        val reason = subArgs.substring(end).trim().takeIf { it.isNotEmpty() } ?: tl("banReason")
        Punishments.banPlayer(uuid, (sender as? Player)?.uniqueId, duration, reason)

        if (duration == null) {
            sender.sendTl("bannedPlayer", name, reason)
        } else {
            sender.sendTl("tempBannedPlayer", name, formatDuration(duration * 1000), reason)
        }
    }
}