package org.betamc.core.commands

import org.betamc.core.config.Property
import org.betamc.core.data.BanData
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
import org.betamc.core.util.format
import org.betamc.core.util.formatError
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.commandapi.sendMessage

import java.time.LocalDateTime
import java.util.UUID
import java.util.regex.Pattern

class CommandBan: Command(
    "ban",
    description = "Bans a player from the server.",
    usage = "/ban <player/uuid> [duration] [reason]",
    permission = "bmc.ban",
    minArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val uuid = if (Utils.UUID_PATTERN.matcher(event.args[0]).matches())
            UUID.fromString(event.args[0]) else Utils.getUUIDFromUsername(event.args[0])

        if (uuid == null) {
            sendMessage(event.sender, formatError("playerNotFound",
                "player" to event.args[0]))
            return
        }

        val name = if (PlayerMap.isPlayerKnown(uuid)) PlayerMap.getPlayer(uuid).name else uuid
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
                    sendMessage(event.sender, format("permanentBan",
                        "user" to name,
                        "reason" to Property.BAN_DEFAULT_REASON))
                }
                else -> {
                    BanData.ban(uuid, reason)
                    sendMessage(event.sender, format("permanentBan",
                        "user" to name, "reason" to reason))
                }
            }
            else -> when (reason.length) {
                0 -> {
                    val until = Utils.parseDateDiff(duration)
                    BanData.ban(uuid, until)
                    sendMessage(event.sender, format("temporaryBan",
                        "user" to name,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to Property.BAN_DEFAULT_REASON))
                }
                else -> {
                    val until = Utils.parseDateDiff(duration)
                    BanData.ban(uuid, until, reason)
                    sendMessage(event.sender, format("temporaryBan",
                        "user" to name,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to reason))
                }
            }
        }
    }
}