package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError

class CommandUnban : Command(
    "unban",
    listOf("pardon"),
    "Unbans a player from the server.",
    "/unban <player/uuid>",
    "zcore.unban",
    minArgs = 1,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val uuid = Utils.getUUIDFromString(event.args[0])
        val name = if (PlayerMap.isPlayerKnown(uuid)) PlayerMap.getPlayer(uuid).name else uuid
        if (!BanData.isBanned(uuid)) {
            sendMessage(event.sender, formatError("userNotBanned"))
            return
        }

        BanData.unban(uuid)
        sendMessage(event.sender, format("userUnbanned", "user" to name))
    }
}