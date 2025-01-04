package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format
import java.time.LocalDateTime
import java.util.regex.Pattern

class CommandBanIP : Command(
    "banip",
    listOf("ipban"),
    "Bans an IP address from the server.",
    "/banip <player/ip> [duration] [reason]",
    "zcore.banip",
    minArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val ip =
            if (Utils.IPV4_PATTERN.matcher(event.args[0]).matches()) {
                event.args[0]
            } else {
                val player = Utils.getPlayerFromString(event.args[0])
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
                    event.sender.sendMessage(format("permanentIpBan",
                        "ip" to ip,
                        "reason" to Config.getString("defaultBanReason")))
                }
                else -> {
                    BanData.banIP(ip, reason)
                    event.sender.sendMessage(format("permanentIpBan",
                        "ip" to ip, "reason" to reason))
                }
            }
            else -> when (reason.length) {
                0 -> {
                    val until = Utils.parseDateDiff(duration)
                    BanData.banIP(ip, until)
                    event.sender.sendMessage(format("temporaryIpBan",
                        "ip" to ip,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to Config.getString("defaultBanReason")))
                }
                else -> {
                    val until = Utils.parseDateDiff(duration)
                    BanData.banIP(ip, until, reason)
                    event.sender.sendMessage(format("temporaryIpBan",
                        "ip" to ip,
                        "duration" to Utils.formatDateDiff(LocalDateTime.now(), until),
                        "reason" to reason))
                }
            }
        }
    }
}