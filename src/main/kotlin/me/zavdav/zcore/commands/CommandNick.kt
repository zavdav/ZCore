package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.colorize
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandNick : AbstractCommand(
    "nick",
    "Changes your nickname.",
    "/nick [player] <nickname>",
    "zcore.nick",
    minArgs = 1,
    maxArgs = 2,
    aliases = listOf("nickname")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var target = player
        var nickname = args[0]

        if (args.size == 2) {
            target = getPlayerFromUsername(args[0])
            nickname = args[1]
        }

        val isSelf = player == target
        assert(isSelf || sender.isAuthorized("zcore.nick.others"), "noPermission")
        val reset = nickname.equals("reset", true) || nickname.equals(target.name, true)
        if (target.isAuthorized("zcore.nick.color")) nickname = colorize(nickname)

        val user = User.from(target)
        if (reset) {
            user.nickname = null
        } else {
            if (isSelf) charge(player)
            user.nickname = nickname
        }
        user.updateDisplayName()

        val rawNick = user.getNick()
        if (!isSelf) {
            if (reset) {
                sender.sendTl("resetNickOther", target)
            } else {
                sender.sendTl("setNickOther", target, "nickname" to rawNick)
            }
        }

        if (reset) {
            target.sendTl("resetNick")
        } else {
            target.sendTl("setNick", "nickname" to rawNick)
        }
    }
}