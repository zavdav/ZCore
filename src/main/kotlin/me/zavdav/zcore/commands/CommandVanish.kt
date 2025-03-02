package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.updateVanishedPlayers
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandVanish : AbstractCommand(
    "vanish",
    "Vanishes you from other players.",
    "/vanish [player]",
    "zcore.vanish",
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var user = User.from(player)

        if (args.isNotEmpty()) {
            val target = getPlayerFromUsername(args[0])
            user = User.from(target)
        }

        val isSelf = player.uniqueId == user.uuid
        assert(isSelf || sender.isAuthorized("zcore.vanish.others"), "noPermission")
        user.isVanished = !user.isVanished
        updateVanishedPlayers()

        if (!isSelf) {
            sender.sendTl(if (user.isVanished) "enabledVanishOther" else "disabledVanishOther", user.player)
        }
        user.player.sendTl(if (user.isVanished) "enabledVanish" else "disabledVanish")
    }
}