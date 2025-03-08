package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandGod : AbstractCommand(
    "god",
    "Toggles your god mode.",
    "/god [player]",
    "zcore.god",
    maxArgs = 1,
    aliases = listOf("godmode")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var user = User.from(player)

        if (args.isNotEmpty()) {
            val target = getPlayerFromUsername(args[0])
            user = User.from(target)
        }

        val isSelf = player.uniqueId == user.uuid
        sender.assertOrSend("noPermission") { isSelf || it.isAuthorized("zcore.god.others") }
        user.isGod = !user.isGod

        if (!isSelf) {
            sender.sendTl(if (user.isGod) "enabledGodOther" else "disabledGodOther", user.player.name)
        }
        user.player.sendTl(if (user.isGod) "enabledGod" else "disabledGod")
    }
}