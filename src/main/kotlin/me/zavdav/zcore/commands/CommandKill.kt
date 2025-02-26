package me.zavdav.zcore.commands

import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission

class CommandKill : ZCoreCommand(
    "kill",
    description = "Kills a player.",
    usage = "/kill [player]",
    permission = "zcore.kill",
    isPlayerOnly = true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var target = player
        if (event.args.isNotEmpty()) {
            target = getPlayerFromUsername(event.args[0])
        }

        val isSelf = player == target
        assert(isSelf || hasPermission(event.sender, "zcore.kill.others"), "noPermission")
        target.health = 0

        if (!isSelf) event.sender.sendTl("killedOther", target)
        target.sendTl("killed")
    }
}