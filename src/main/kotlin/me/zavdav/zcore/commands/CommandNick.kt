package me.zavdav.zcore.commands

import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.hasPermission

class CommandNick : ZCoreCommand(
    "nick",
    listOf("nickname"),
    "Changes your nickname.",
    "/nick [player] <nickname>",
    "zcore.nick",
    true,
    1,
    2
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var target = player
        var nickname = event.args[0]

        if (event.args.size == 2) {
            target = getPlayerFromUsername(event.args[0])
            nickname = event.args[1]
        }

        val isSelf = player == target
        assert(isSelf || hasPermission(event.sender, "zcore.nick.others"), "noPermission")
        val reset = nickname.equals("reset", true) || nickname.equals(target.name, true)
        if (hasPermission(target, "zcore.nick.color")) nickname = colorize(nickname)

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
                event.sender.sendTl("resetNickOther", target)
            } else {
                event.sender.sendTl("setNickOther", target, "nickname" to rawNick)
            }
        }

        if (reset) {
            target.sendTl("resetNick")
        } else {
            target.sendTl("setNick", "nickname" to rawNick)
        }
    }
}