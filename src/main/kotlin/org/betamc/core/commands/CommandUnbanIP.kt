package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.data.BanData
import org.betamc.core.util.Utils
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage

class CommandUnbanIP : Command(
    "unbanip",
    listOf("unipban", "pardonip"),
    "Unbans an IP address from the server.",
    "/unbanip <ip>",
    "bmc.unbanip",
    minArgs = 1,
    maxArgs = 1,
    preprocessor = Preprocessor()){

    override fun execute(event: CommandEvent) {
        if (!Utils.IPV4_PATTERN.matcher(event.args[0]).matches()) {
            sendMessage(event.sender, Utils.format(Language.UNBANIP_INVALID_IP))
            return
        }

        val ip = event.args[0]
        if (!BanData.isIPBanned(ip)) {
            sendMessage(event.sender, Language.UNBANIP_NOT_BANNED)
            return
        }

        BanData.unbanIP(ip)
        sendMessage(event.sender, Utils.format(Language.UNBANIP_SUCCESS, ip))
    }
}