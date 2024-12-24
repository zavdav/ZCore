package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError
import java.util.UUID

class CommandUnbanIP : Command(
    "unbanip",
    listOf("unipban", "pardonip"),
    "Unbans an IP address from the server.",
    "/unbanip <player/ip>",
    "zcore.unbanip",
    minArgs = 1,
    maxArgs = 1,
    preprocessor = Preprocessor()){

    override fun execute(event: CommandEvent) {
        val ip =
            if (Utils.IPV4_PATTERN.matcher(event.args[0]).matches()) {
                event.args[0]
            } else {
                val uuid = if (Utils.UUID_PATTERN.matcher(event.args[0]).matches())
                    UUID.fromString(event.args[0])
                    else Utils.getUUIDFromUsername(event.args[0])
                if (uuid == null) {
                    sendMessage(event.sender, formatError("playerNotFound",
                        "player" to event.args[0]))
                    return
                }
                val ipBan = BanData.getIPBan(uuid)
                if (ipBan == null) {
                    sendMessage(event.sender, formatError("ipNotBanned"))
                    return
                }
                ipBan.ip
            }

        if (!BanData.isIPBanned(ip)) {
            sendMessage(event.sender, formatError("ipNotBanned"))
            return
        }

        BanData.unbanIP(ip)
        sendMessage(event.sender, format("ipUnbanned", "ip" to ip))
    }
}