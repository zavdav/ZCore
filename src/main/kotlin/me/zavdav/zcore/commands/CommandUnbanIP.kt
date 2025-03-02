package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.IPV4_PATTERN
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender

class CommandUnbanIP : AbstractCommand(
    "unbanip",
    "Unbans an IP address from the server.",
    "/unbanip <player|ip>",
    "zcore.unbanip",
    false,
    1,
    1,
    listOf("unipban", "pardonip")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val ip =
            if (IPV4_PATTERN.matcher(args[0]).matches()) {
                args[0]
            } else {
                val uuid = getUUIDFromString(args[0])
                val ipBan = Punishments.getIPBan(uuid)
                assert(ipBan != null, "ipNotBanned", "user" to uuid)
                ipBan!!.ip
            }

        assert(Punishments.isIPBanned(ip), "ipNotBanned", "user" to ip)
        Punishments.unbanIP(ip)
        sender.sendTl("unbannedIp", "ip" to ip)
    }
}