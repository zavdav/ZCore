package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandGod : ZCoreCommand(
    "god",
    listOf("godmode"),
    "Enables god mode.",
    "/god [player]",
    "zcore.god",
    true,
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
        assert(isSelf || hasPermission(event.sender, "zcore.god.others"), "noPermission")
        user.isGod = !user.isGod

        if (!isSelf) {
            event.sender.sendTl(if (user.isGod)
                "enabledGodOther" else "disabledGodOther", user.player)
        }
        user.player.sendTl(if (user.isGod) "enabledGod" else "disabledGod")
    }
}