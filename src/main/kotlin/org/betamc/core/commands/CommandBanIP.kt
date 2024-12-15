package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.data.BanData
import org.betamc.core.util.Utils
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.commandapi.sendMessage
import java.time.LocalDateTime
import java.util.UUID
import java.util.regex.Pattern

class CommandBanIP : Command(
    "banip",
    listOf("ipban"),
    "Bans an IP address from the server.",
    "/banip <player/ip> [duration] [reason]",
    "bmc.banip",
    minArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val ip =
            if (Utils.IPV4_PATTERN.matcher(event.args[0]).matches()) {
                event.args[0]
            } else {
                val player = if (Utils.UUID_PATTERN.matcher(event.args[0]).matches())
                    Utils.getPlayerFromUUID(UUID.fromString(event.args[0]))
                    else Utils.getPlayerFromUsername(event.args[0])
                if (player == null) {
                    sendMessage(event.sender, Utils.format(Language.PLAYER_NOT_FOUND, event.args[0]))
                    return
                }
                player.address.address.hostAddress
            }

        val subArgs = joinArgs(event.args , 1, event.args.size)
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
                    BanData.banIP(ip)
                    sendMessage(event.sender, Utils.format(Language.BANIP_SUCCESS,
                        ip, "permanently", ""))
                }
                else -> {
                    BanData.banIP(ip, reason)
                    sendMessage(event.sender, Utils.format(Language.BANIP_SUCCESS,
                        ip, "permanently", ", reason: $reason"))
                }
            }
            else -> when (reason.length) {
                0 -> {
                    val until = Utils.parseDateDiff(duration)
                    BanData.banIP(ip, until)
                    sendMessage(event.sender, Utils.format(Language.BANIP_SUCCESS,
                        ip, "for ${Utils.formatDateDiff(LocalDateTime.now(), until)}", ""))
                }
                else -> {
                    val until = Utils.parseDateDiff(duration)
                    BanData.banIP(ip, until, reason)
                    sendMessage(event.sender, Utils.format(Language.BANIP_SUCCESS,
                        ip, "for ${Utils.formatDateDiff(LocalDateTime.now(), until)}", ", reason: $reason"))
                }
            }
        }
    }
}