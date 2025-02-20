package me.zavdav.zcore.commands

import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
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
            val target = Utils.getPlayerFromUsername(event.args[0])
            user = User.from(target)
        }

        val isSelf = player.uniqueId == user.uuid
        assert(isSelf || hasPermission(event.sender, "zcore.vanish.others"), "noPermission")
        user.vanished = !user.vanished
        Utils.updateVanishedPlayers()

        if (!isSelf) {
            event.sender.sendTl(if (user.vanished)
                "enabledVanishOther" else "disabledVanishOther", user.player)
        }
        user.player.sendTl(if (user.vanished) "enabledVanish" else "disabledVanish")
    }
}