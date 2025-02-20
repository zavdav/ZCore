package me.zavdav.zcore.commands

import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission

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
                "enabledChatOther" else "disabledChatOther", user.player)
        }
        user.player.sendTl(if (user.seesChat) "enabledChat" else "disabledChat")
    }
}