package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.isSelf
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatString
import org.poseidonplugins.zcore.util.getMessage

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
        if (!isSelf && !hasPermission(event.sender, "zcore.nick.others")) {
            sendMessage(event.sender, format("noPermission"))
            return
        }

        val reset = nickname.equals("reset", true) || nickname.equals(target.name, true)
        if (hasPermission(event.sender, "zcore.nick.color")) nickname = colorize(nickname)
        val zPlayer = PlayerMap.getPlayer(target)

        if (reset) zPlayer.resetNickname() else zPlayer.nickname = nickname
        zPlayer.updateDisplayName()
        if (!isSelf) {
            event.sender.sendMessage(if (reset)
                format("resetNickOther", "player" to target.name)
                else formatString(colorize(getMessage("setNickOther")),
                    "player" to target.name,
                    "nickname" to "${colorize(Config.getString("nickPrefix"))}$nickname", color = false))
        }
        target.sendMessage(if (reset) format("resetNick")
            else formatString(colorize(getMessage("setNick")),
                "nickname" to "${colorize(Config.getString("nickPrefix"))}$nickname", color = false))
    }
}