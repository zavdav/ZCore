package me.zavdav.zcore.commands

import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandTpDeny : ZCoreCommand(
    "tpdeny",
    listOf("tpno"),
    "Denies your current teleport request.",
    "/tpdeny",
    "zcore.tpdeny",
    true,
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val user = User.from(player)
        val request = user.tpRequest
        assert(request != null, "noTpRequest")

        user.tpRequest = null
        val target = request!!.first
        player.sendTl("deniedTpRequest", target)
        target.sendTl("otherDeniedRequest", player)
    }
}