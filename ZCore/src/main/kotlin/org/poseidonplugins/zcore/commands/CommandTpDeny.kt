package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

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