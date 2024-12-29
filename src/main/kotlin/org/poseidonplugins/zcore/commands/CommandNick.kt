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

        val reset = nickname.equals("reset", true)
        if (hasPermission(event.sender, "zcore.nick.color")) nickname = colorize(nickname)
        if (reset) {
            target.displayName = target.name
            PlayerMap.getPlayer(target).resetNickname()
        } else {
            target.displayName = "§f${colorize(Config.getString("nickPrefix"))}$nickname"
            PlayerMap.getPlayer(target).nickname = nickname
        }

        if (!isSelf) {
            event.sender.sendMessage(if (reset)
                format("resetNickOther", "player" to target.name)
                else formatString(colorize(getMessage("setNickOther")),
                    "player" to target.name, "nickname" to target.displayName, color = false)
            )
        }
        target.sendMessage(if (reset) format("resetNick")
            else formatString(colorize(getMessage("setNick")),
                "nickname" to target.displayName, color = false))
    }
}