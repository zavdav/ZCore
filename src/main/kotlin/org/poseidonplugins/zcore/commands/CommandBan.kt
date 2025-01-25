package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.user.UserMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.sendTl
import java.time.LocalDateTime
import java.util.regex.Pattern

class CommandBan: ZCoreCommand(
    "ban",
    description = "Bans a player from the server.",
    usage = "/ban <player/uuid> [duration] [reason]",
    permission = "zcore.ban",
    minArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val uuid = Utils.getUUIDFromString(event.args[0])
        val name = if (UserMap.isUserKnown(uuid)) User.from(uuid).name else uuid
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
                    BanData.ban(uuid)
                    event.sender.sendTl("permanentBan",
                        "user" to name,
                        "reason" to Config.getString("defaultBanReason"))
                }
                else -> {
                    BanData.ban(uuid, reason)
                    event.sender.sendTl("permanentBan", "user" to name, "reason" to reason)
                }
            }
            else -> when (reason.length) {
                0 -> {
                    val until = Utils.parseDateDiff(duration)
                    BanData.ban(uuid, until)
                    event.sender.sendTl("temporaryBan",
                        "user" to name,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to Config.getString("defaultBanReason"))
                }
                else -> {
                    val until = Utils.parseDateDiff(duration)
                    BanData.ban(uuid, until, reason)
                    event.sender.sendTl("temporaryBan",
                        "user" to name,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to reason)
                }
            }
        }
    }
}