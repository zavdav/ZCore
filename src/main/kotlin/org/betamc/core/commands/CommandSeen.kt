package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
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
            sendMessage(event.sender, Utils.format(Language.PLAYER_NOT_FOUND, event.args[0]))
            return
        }

        val bmcPlayer = PlayerMap.getPlayer(uuid)
        if (bmcPlayer.isOnline) {
            val isSelf = event.sender is Player && (event.sender as Player).uniqueId == bmcPlayer.uuid
            sendMessage(event.sender, Utils.format(Language.SEEN_ONLINE,
                if (isSelf) "You have" else "${bmcPlayer.name} has",
                Utils.formatDateDiff(bmcPlayer.lastSeen, LocalDateTime.now())))
        } else {
            sendMessage(event.sender, Utils.format(Language.SEEN_OFFLINE,
                bmcPlayer.name, Utils.formatDateDiff(bmcPlayer.lastSeen, LocalDateTime.now())))
        }
    }
}