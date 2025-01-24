package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.PlayerNotFoundException
import org.poseidonplugins.zcore.user.UserMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.sendTl
import java.time.LocalDateTime

class CommandSeen : Command(
    "seen",
    description = "Shows when a player was last online.",
    usage = "/seen <player>",
    permission = "zcore.seen",
    minArgs = 1,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val uuid = Utils.getUUIDFromUsername(event.args[0])
        if (!UserMap.isUserKnown(uuid)) {
            throw PlayerNotFoundException(event.args[0])
        }

        val user = User.from(uuid)
        if (user.isOnline) {
            val isSelf = event.sender is Player && (event.sender as Player).uniqueId == user.uuid
            val duration = Utils.formatDateDiff(user.lastSeen, LocalDateTime.now())

            if (isSelf) {
                event.sender.sendTl("seenOnline", "duration" to duration)
            } else {
                event.sender.sendTl("seenOnlineOther", user.player, "duration" to duration)
            }
        } else {
            event.sender.sendTl("seenOffline",
                "user" to user.name,
                "duration" to Utils.formatDateDiff(user.lastSeen, LocalDateTime.now()))
        }
    }
}