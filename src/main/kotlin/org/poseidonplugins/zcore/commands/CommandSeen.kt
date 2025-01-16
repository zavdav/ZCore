package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.util.PlayerNotFoundException
import org.poseidonplugins.zcore.player.PlayerMap
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
        if (!PlayerMap.isPlayerKnown(uuid)) {
            throw PlayerNotFoundException(event.args[0])
        }

        val zPlayer = PlayerMap.getPlayer(uuid)
        if (zPlayer.isOnline) {
            val isSelf = event.sender is Player && (event.sender as Player).uniqueId == zPlayer.uuid
            val duration = Utils.formatDateDiff(zPlayer.lastSeen, LocalDateTime.now())

            if (isSelf) {
                event.sender.sendTl("seenOnline", "duration" to duration)
            } else {
                event.sender.sendTl("seenOnlineOther", zPlayer.onlinePlayer, "duration" to duration)
            }
        } else {
            event.sender.sendTl("seenOffline",
                "user" to zPlayer.name,
                "duration" to Utils.formatDateDiff(zPlayer.lastSeen, LocalDateTime.now()))
        }
    }
}