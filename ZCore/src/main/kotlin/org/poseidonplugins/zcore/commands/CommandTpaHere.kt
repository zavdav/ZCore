package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.sendTl

class CommandTpaHere : ZCoreCommand(
    "tpahere",
    description = "Sends a request for a player to teleport to you.",
    usage = "/tpahere <player>",
    permission = "zcore.tpahere",
    isPlayerOnly = true,
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val target = Utils.getPlayerFromUsername(event.args[0])
        val targetUser = User.from(target)
        player.sendTl("tpRequestSent", target)

        if (player.uniqueId !in targetUser.ignores ||
            hasPermission(player, "zcore.ignore.exempt")) {
            targetUser.tpRequest = player to User.TeleportType.TPAHERE
            target.sendTl("tpaHereRequest", player)
            target.sendTl("tpaUsage")
        }
    }
}