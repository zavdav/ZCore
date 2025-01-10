package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.*

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
            event.sender.sendErrTl("userNotBanned")
            return
        }

        BanData.unban(uuid)
        event.sender.sendTl("userUnbanned", "user" to name)
    }
}