package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.getUUIDFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSeen : AbstractCommand(
    "seen",
    "Shows when a player was last online.",
    "/seen <player>",
    "zcore.seen",
    false,
    1,
    1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val uuid = getUUIDFromUsername(args[0])

        val user = User.from(uuid)
        if (user.isOnline) {
            val isSelf = sender is Player && sender.uniqueId == user.uuid
            val duration = formatDuration(System.currentTimeMillis() - user.lastJoin)

            if (isSelf) {
                sender.sendTl("seenOnline", duration)
            } else {
                sender.sendTl("seenOnlineOther", user.player.name, duration)
            }
        } else {
            sender.sendTl("seenOffline",
                user.name, formatDuration(System.currentTimeMillis() - user.lastSeen))
        }
    }
}