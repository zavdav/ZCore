package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.isSelf
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandNick : Command(
    "nick",
    listOf("nickname"),
    "Sets your or a player's nickname.",
    "/nick [player] <nickname>",
    "zcore.nick",
    true,
    1,
    2,
    Preprocessor()) {

    override fun execute(event: CommandEvent) {
        var target = event.sender as Player
        var nickname = event.args[0]

        if (event.args.size == 2) {
            target = Utils.getPlayerFromUsername(event.args[0])
            nickname = event.args[1]
        }

        val isSelf = (event.sender as Player).isSelf(target)
        assert(isSelf || hasPermission(event.sender, "zcore.nick.others"), "noPermission")
        val reset = nickname.equals("reset", true) || nickname.equals(target.name, true)
        if (hasPermission(event.sender, "zcore.nick.color")) nickname = colorize(nickname)

        val user = User.from(target)
        if (reset) user.resetNickname() else user.nickname = nickname
        user.updateDisplayName()

        val rawNick = "${Config.getString("nickPrefix")}$nickname"
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