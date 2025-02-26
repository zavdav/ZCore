package me.zavdav.zcore.commands

import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.updateVanishedPlayers
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission

class CommandVanish : ZCoreCommand(
    "vanish",
    description = "Vanishes you from other players.",
    usage = "/vanish [player]",
    permission = "zcore.vanish",
    isPlayerOnly = true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var user = User.from(player)

        if (event.args.isNotEmpty()) {
            val target = getPlayerFromUsername(event.args[0])
            user = User.from(target)
        }

        val isSelf = player.uniqueId == user.uuid
        assert(isSelf || hasPermission(event.sender, "zcore.vanish.others"), "noPermission")
        user.isVanished = !user.isVanished
        updateVanishedPlayers()

        if (!isSelf) {
            event.sender.sendTl(if (user.isVanished)
                "enabledVanishOther" else "disabledVanishOther", user.player)
        }
        user.player.sendTl(if (user.isVanished) "enabledVanish" else "disabledVanish")
    }
}