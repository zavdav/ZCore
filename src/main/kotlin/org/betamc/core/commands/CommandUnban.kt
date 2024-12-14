package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.data.BanData
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage
import java.util.UUID

class CommandUnban : Command(
    "unban",
    listOf("pardon"),
    "Unbans a player from the server.",
    "/unban <player/uuid>",
    "bmc.unban",
    minArgs = 1,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val uuid = if (Utils.UUID_PATTERN.matcher(event.args[0]).matches())
            UUID.fromString(event.args[0]) else Utils.getUUIDFromUsername(event.args[0])

        if (uuid == null) {
            sendMessage(event.sender, Utils.format(Language.PLAYER_NOT_FOUND, event.args[0]))
            return
        }

        val name = if (PlayerMap.isPlayerKnown(uuid)) PlayerMap.getPlayer(uuid).name else uuid
        if (!BanData.isBanned(uuid)) {
            sendMessage(event.sender, Language.UNBAN_NOT_BANNED)
            return
        }

        BanData.unban(uuid)
        sendMessage(event.sender, Utils.format(Language.UNBAN_SUCCESS, name))
    }
}