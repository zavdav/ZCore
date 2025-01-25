package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandToggleChat : ZCoreCommand(
    "togglechat",
    listOf("tc"),
    "Toggles whether or not you see public chat.",
    "/togglechat [player]",
    "zcore.togglechat",
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
        assert(isSelf || hasPermission(event.sender, "zcore.togglechat.others"), "noPermission")
        user.seesChat = !user.seesChat

        if (!isSelf) {
            event.sender.sendTl(if (user.seesChat)
                "chatEnabledOther" else "chatDisabledOther", user.player)
        }
        user.player.sendTl(if (user.seesChat) "chatEnabled" else "chatDisabled")
    }
}