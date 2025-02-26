package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.user.User
import me.zavdav.zcore.user.UserMap
import me.zavdav.zcore.util.TIME_PATTERN
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.parseDuration
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.joinArgs
import java.util.regex.Pattern

class CommandBan: ZCoreCommand(
    "ban",
    description = "Bans a player from the server.",
    usage = "/ban <player> [duration] [reason]",
    permission = "zcore.ban",
    minArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val uuid = getUUIDFromString(event.args[0])
        var name = uuid.toString()

        if (UserMap.isUserKnown(uuid)) {
            assert(event.sender !is Player || (event.sender as Player).uniqueId != uuid, "cannotBanSelf")
            val user = User.from(uuid)
            name = user.name
            if (user.isOnline) {
                assert(!hasPermission(user.player, "zcore.ban.exempt"), "cannotBanUser", "name" to name)
            } else {
                assert(!user.banExempt, "cannotBanUser", "name" to name)
            }
        }

        val subArgs = joinArgs(event.args, 1, event.args.size)
        val matcher = Pattern.compile("^${TIME_PATTERN.pattern()}").matcher(subArgs)
        val sb = StringBuilder()
        var end = 0

        while (matcher.find() && matcher.start() == end) {
            sb.append(subArgs, matcher.start(), matcher.end())
            end = matcher.end()
        }

        val duration = if (sb.toString().isNotEmpty()) parseDuration(sb.toString()) else null
        val reason = subArgs.substring(end).trim().takeIf { it.isNotEmpty() } ?: tl("banReason")
        Punishments.banPlayer(uuid, (event.sender as? Player)?.uniqueId, duration, reason)

        if (duration == null) {
            event.sender.sendTl("bannedPlayer", "user" to name, "reason" to reason)
        } else {
            event.sender.sendTl("tempBannedPlayer",
                "user" to name,
                "duration" to formatDuration(duration * 1000),
                "reason" to reason)
        }
    }
}