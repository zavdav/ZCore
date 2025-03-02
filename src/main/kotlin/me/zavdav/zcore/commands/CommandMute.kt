package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.TIME_PATTERN
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.getUUIDFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.joinArgs
import me.zavdav.zcore.util.parseDuration
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.regex.Pattern

class CommandMute : AbstractCommand(
    "mute",
    "Mutes a player, preventing them from chatting.",
    "/mute <player> [duration] [reason]",
    "zcore.mute",
    false,
    1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val uuid = getUUIDFromUsername(args[0])
        val name: String

        assert(sender !is Player || sender.uniqueId != uuid, "cannotMuteSelf")
        val user = User.from(uuid)
        name = user.name
        if (user.isOnline) {
            assert(!user.player.isAuthorized("zcore.mute.exempt"), "cannotMuteUser", "name" to name)
        } else {
            assert(!user.muteExempt, "cannotMuteUser", "name" to name)
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
        val reason = subArgs.substring(end).trim().takeIf { it.isNotEmpty() } ?: tl("muteReason")
        Punishments.mutePlayer(uuid, (sender as? Player)?.uniqueId, duration, reason)

        if (duration == null) {
            sender.sendTl("mutedPlayer", "user" to name, "reason" to reason)
        } else {
            sender.sendTl("tempMutedPlayer",
                "user" to name,
                "duration" to formatDuration(duration * 1000),
                "reason" to reason)
        }
    }
}