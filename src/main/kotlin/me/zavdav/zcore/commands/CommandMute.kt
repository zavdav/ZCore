package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.TIME_PATTERN
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.parseDuration
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.joinArgs
import java.util.regex.Pattern

class CommandMute : ZCoreCommand(
    "mute",
    description = "Mutes a player, preventing them from chatting.",
    usage = "/mute <player> [duration] [reason]",
    permission = "zcore.mute",
    minArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val uuid = Utils.getUUIDFromUsername(event.args[0])
        val name: String

        assert(event.sender !is Player || (event.sender as Player).uniqueId != uuid, "cannotMuteSelf")
        val user = User.from(uuid)
        name = user.name
        if (user.isOnline) {
            assert(!hasPermission(user.player, "zcore.mute.exempt"), "cannotMuteUser", "name" to name)
        } else {
            assert(!user.muteExempt, "cannotMuteUser", "name" to name)
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
        val reason = subArgs.substring(end).trim().takeIf { it.isNotEmpty() } ?: tl("muteReason")
        Punishments.mutePlayer(uuid, (event.sender as? Player)?.uniqueId, duration, reason)

        if (duration == null) {
            event.sender.sendTl("mutedPlayer", "user" to name, "reason" to reason)
        } else {
            event.sender.sendTl("tempMutedPlayer",
                "user" to name,
                "duration" to formatDuration(duration * 1000),
                "reason" to reason)
        }
    }
}