package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getUUIDFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender

class CommandUnmute : AbstractCommand(
    "unmute",
    "Unmutes a player, allowing them to chat again.",
    "/unmute <player>",
    "zcore.unmute",
    false,
    1,
    1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val uuid = getUUIDFromUsername(args[0])
        val user = User.from(uuid)
        sender.assertOrSend("userNotMuted", user.name) { Punishments.isPlayerMuted(uuid) }
        Punishments.unmutePlayer(uuid)
        sender.sendTl("unmutedPlayer", user.name)
    }
}