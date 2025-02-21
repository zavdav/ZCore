package me.zavdav.zcore.commands

import me.zavdav.zcore.user.User
import me.zavdav.zcore.user.UserMap
import me.zavdav.zcore.util.PlayerNotFoundException
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandSeen : ZCoreCommand(
    "seen",
    description = "Shows when a player was last online.",
    usage = "/seen <player>",
    permission = "zcore.seen",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val uuid = Utils.getUUIDFromUsername(event.args[0])
        if (!UserMap.isUserKnown(uuid)) {
            throw PlayerNotFoundException(event.args[0])
        }

        val user = User.from(uuid)
        if (user.isOnline) {
            val isSelf = event.sender is Player && (event.sender as Player).uniqueId == user.uuid
            val duration = Utils.formatDuration(System.currentTimeMillis() - user.lastJoin)

            if (isSelf) {
                event.sender.sendTl("seenOnline", "duration" to duration)
            } else {
                event.sender.sendTl("seenOnlineOther", user.player, "duration" to duration)
            }
        } else {
            event.sender.sendTl("seenOffline",
                "user" to user.name,
                "duration" to Utils.formatDuration(System.currentTimeMillis() - user.lastSeen))
        }
    }
}