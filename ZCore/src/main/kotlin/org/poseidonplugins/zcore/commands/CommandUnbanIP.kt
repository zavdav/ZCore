package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.Punishments
import org.poseidonplugins.zcore.util.*

class CommandUnbanIP : ZCoreCommand(
    "unbanip",
    listOf("unipban", "pardonip"),
    "Unbans an IP address from the server.",
    "/unbanip <player|ip>",
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
                val ipBan = Punishments.getIPBan(uuid)
                assert(ipBan != null, "ipNotBanned")
                ipBan!!.ip
            }

        assert(Punishments.isIPBanned(ip), "ipNotBanned")
        Punishments.unbanIP(ip)
        event.sender.sendTl("ipUnbanned", "ip" to ip)
    }
}