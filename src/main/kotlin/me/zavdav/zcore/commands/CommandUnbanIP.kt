package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.util.IPV4_PATTERN
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.sendTl
import org.poseidonplugins.commandapi.CommandEvent

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
            if (IPV4_PATTERN.matcher(event.args[0]).matches()) {
                event.args[0]
            } else {
                val uuid = getUUIDFromString(event.args[0])
                val ipBan = Punishments.getIPBan(uuid)
                assert(ipBan != null, "ipNotBanned", "user" to uuid)
                ipBan!!.ip
            }

        assert(Punishments.isIPBanned(ip), "ipNotBanned", "user" to ip)
        Punishments.unbanIP(ip)
        event.sender.sendTl("unbannedIp", "ip" to ip)
    }
}