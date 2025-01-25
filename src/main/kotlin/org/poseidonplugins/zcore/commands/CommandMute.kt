package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.data.Punishments
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
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
        val name = User.from(uuid).name
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
                    event.sender.sendTl("permanentMute",
                        "user" to name,
                        "reason" to Config.getString("defaultMuteReason"))
                }
                else -> {
                    Punishments.mute(uuid, reason)
                    event.sender.sendTl("permanentMute", "user" to name, "reason" to reason)
                }
            }
            else -> when (reason.length) {
                0 -> {
                    val until = Utils.parseDateDiff(duration)
                    Punishments.mute(uuid, until)
                    event.sender.sendTl("temporaryMute",
                        "user" to name,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to Config.getString("defaultMuteReason"))
                }
                else -> {
                    val until = Utils.parseDateDiff(duration)
                    Punishments.mute(uuid, until, reason)
                    event.sender.sendTl("temporaryMute",
                        "user" to name,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to reason)
                }
            }
        }
    }
}