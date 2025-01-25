package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.util.*

class CommandUnbanIP : ZCoreCommand(
    "unbanip",
    listOf("unipban", "pardonip"),
    "Unbans an IP address from the server.",
    "/unbanip <player/ip>",
    "zcore.unbanip",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val ip =
            if (Utils.IPV4_PATTERN.matcher(event.args[0]).matches()) {
                event.args[0]
            } else {
                val uuid = Utils.getUUIDFromString(event.args[0])
                val ipBan = BanData.getIPBan(uuid)
                assert(ipBan != null, "ipNotBanned")
                ipBan!!.ip
            }

        assert(BanData.isIPBanned(ip), "ipNotBanned")
        BanData.unbanIP(ip)
        event.sender.sendTl("ipUnbanned", "ip" to ip)
    }
}