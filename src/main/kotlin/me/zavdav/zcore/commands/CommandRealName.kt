package me.zavdav.zcore.commands

import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.Bukkit
import org.poseidonplugins.commandapi.CommandEvent

class CommandRealName : ZCoreCommand(
    "realname",
    description = "Shows the real name of a nicked player.",
    usage = "/realname <nickname>",
    permission = "zcore.realname",
    isPlayerOnly = true,
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val string = event.args[0].trim()
        var matches = 0
        for (player in Bukkit.getOnlinePlayers()) {
            val user = User.from(player)
            val nickname = user.getNick()
                .replace("§([0-9a-f])".toRegex(), "")

            if (string.equals(nickname, true)) {
                event.sender.sendTl("realName", user.player)
                matches++
            }
        }
        assert(matches > 0, "nickNotFound", "nickname" to string)
    }
}