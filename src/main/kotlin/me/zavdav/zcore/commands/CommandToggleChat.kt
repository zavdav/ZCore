package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandToggleChat : AbstractCommand(
    "togglechat",
    "Toggles whether or not you see public chat.",
    "/togglechat [player]",
    "zcore.togglechat",
    maxArgs = 1,
    aliases = listOf("tc")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var user = User.from(player)

        if (args.isNotEmpty()) {
            val target = getPlayerFromUsername(args[0])
            user = User.from(target)
        }

        val isSelf = player.uniqueId == user.uuid
        assert(isSelf || sender.isAuthorized("zcore.togglechat.others"), "noPermission")
        user.seesChat = !user.seesChat

        if (!isSelf) {
            sender.sendTl(if (user.seesChat) "enabledChatOther" else "disabledChatOther", user.player.name)
        }
        user.player.sendTl(if (user.seesChat) "enabledChat" else "disabledChat")
    }
}