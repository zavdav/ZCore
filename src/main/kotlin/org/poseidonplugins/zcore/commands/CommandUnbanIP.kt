package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError

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
                val uuid = Utils.getUUIDFromString(event.args[0])
                val ipBan = BanData.getIPBan(uuid)
                if (ipBan == null) {
                    event.sender.sendMessage(formatError("ipNotBanned"))
                    return
                }
                ipBan.ip
            }

        if (!BanData.isIPBanned(ip)) {
            event.sender.sendMessage(formatError("ipNotBanned"))
            return
        }

        BanData.unbanIP(ip)
        event.sender.sendMessage(format("ipUnbanned", "ip" to ip))
    }
}