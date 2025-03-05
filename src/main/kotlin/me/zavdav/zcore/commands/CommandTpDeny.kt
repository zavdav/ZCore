package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpDeny : AbstractCommand(
    "tpdeny",
    "Denies your current teleport request.",
    "/tpdeny",
    "zcore.tpdeny",
    maxArgs = 0,
    aliases = listOf("tpno")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val user = User.from(player)
        val request = user.tpRequest
        assert(request != null, "noTpRequest")

        user.tpRequest = null
        val target = request!!.first
        player.sendTl("deniedTpRequest", target.name)
        target.sendTl("otherDeniedRequest", player.name)
    }
}