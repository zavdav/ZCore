package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class CommandRealName : AbstractCommand(
    "realname",
    "Shows the real name of a nicked player.",
    "/realname <nickname>",
    "zcore.realname",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val string = args[0].trim()
        var matches = 0
        for (player in Bukkit.getOnlinePlayers()) {
            val user = User.from(player)
            val nickname = user.getNick()
                .replace("§([0-9a-f])".toRegex(), "")

            if (string.equals(nickname, true)) {
                sender.sendTl("realName", user.player)
                matches++
            }
        }
        assert(matches > 0, "nickNotFound", "nickname" to string)
    }
}