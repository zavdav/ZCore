package org.poseidonplugins.zcore.commands

import org.bukkit.Bukkit
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.formatError
import org.poseidonplugins.zcore.util.formatString
import org.poseidonplugins.zcore.util.getMessage

class CommandRealName : Command(
    "realname",
    description = "Shows the real name of a nicked player.",
    usage = "/realname <nickname>",
    permission = "zcore.realname",
    isPlayerOnly = true,
    minArgs = 1,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val string = event.args[0].trim()
        var matches = 0
        for (player in Bukkit.getOnlinePlayers()) {
            val zPlayer = PlayerMap.getPlayer(player)
            val nickname = "${colorize(Config.getString("nickPrefix"))}${zPlayer.nickname}"
                .replace("§([0-9a-f])".toRegex(), "")

            if (string.equals(nickname, true)) {
                event.sender.sendMessage(formatString(colorize(getMessage("realName")),
                    "displayname" to zPlayer.displayName, "username" to zPlayer.name))
                matches++
            }
        }
        if (matches == 0) sendMessage(event.sender, formatError("nickNotFound", "nickname" to string))
    }
}