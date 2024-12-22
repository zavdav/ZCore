package org.betamc.core.commands

import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
import org.betamc.core.util.format
import org.betamc.core.util.formatError
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage
import java.time.LocalDateTime

class CommandSeen : Command(
    "seen",
    description = "Shows when a player was last online.",
    usage = "/seen <player>",
    permission = "bmc.seen",
    minArgs = 1,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val uuid = Utils.getUUIDFromUsername(event.args[0])
        if (uuid == null || !PlayerMap.isPlayerKnown(uuid)) {
            sendMessage(event.sender, formatError("playerNotFound",
                "player" to event.args[0]))
            return
        }

        val bmcPlayer = PlayerMap.getPlayer(uuid)
        if (bmcPlayer.isOnline) {
            val isSelf = event.sender is Player && (event.sender as Player).uniqueId == bmcPlayer.uuid
            val duration = Utils.formatDateDiff(bmcPlayer.lastSeen, LocalDateTime.now())
            sendMessage(event.sender, if (isSelf)
                format("seenOnline", "duration" to duration)
                else format("seenOnlineOther",
                    "player" to bmcPlayer.name,
                    "duration" to duration))
        } else {
            sendMessage(event.sender, format("seenOffline",
                "user" to bmcPlayer.name,
                "duration" to Utils.formatDateDiff(bmcPlayer.lastSeen, LocalDateTime.now())))
        }
    }
}