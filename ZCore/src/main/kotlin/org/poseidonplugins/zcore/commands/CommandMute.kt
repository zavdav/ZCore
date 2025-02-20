package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.zcore.data.Punishments
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.tl
import org.poseidonplugins.zcore.util.sendTl
import java.time.LocalDateTime
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
        val matcher = Pattern.compile("^${Utils.TIME_PATTERN.pattern()}").matcher(subArgs)
        val sb = StringBuilder()
        var end = 0

        while (matcher.find() && matcher.start() == end) {
            sb.append(subArgs, matcher.start(), matcher.end())
            end = matcher.end()
        }

        val duration = sb.toString()
        val reason = subArgs.substring(end)

        when (duration.length) {
            0 -> when (reason.length) {
                0 -> {
                    Punishments.mute(uuid)
                    event.sender.sendTl("mutedPlayer",
                        "user" to name,
                        "reason" to tl("muteReason"))
                }
                else -> {
                    Punishments.mute(uuid, reason)
                    event.sender.sendTl("mutedPlayer", "user" to name, "reason" to reason)
                }
            }
            else -> when (reason.length) {
                0 -> {
                    val until = Utils.parseDateDiff(duration)
                    Punishments.mute(uuid, until)
                    event.sender.sendTl("tempMutedPlayer",
                        "user" to name,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to tl("muteReason"))
                }
                else -> {
                    val until = Utils.parseDateDiff(duration)
                    Punishments.mute(uuid, until, reason)
                    event.sender.sendTl("tempMutedPlayer",
                        "user" to name,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to reason)
                }
            }
        }
    }
}