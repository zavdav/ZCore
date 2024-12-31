package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError

class CommandIgnore : Command(
    "ignore",
    description = "Makes you ignore a player.",
    usage = "/ignore <player>",
    permission = "zcore.ignore",
    isPlayerOnly = true,
    minArgs = 1,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val zPlayer = PlayerMap.getPlayer(event.sender as Player)
        val uuid = Utils.getUUIDFromUsername(event.args[0])
        if (zPlayer.uuid == uuid) {
            sendMessage(event.sender, formatError("cannotIgnoreSelf"))
            return
        }

        if (uuid in zPlayer.ignores) {
            zPlayer.setIgnored(uuid, false)
            sendMessage(event.sender, format("notIgnoringPlayer",
                "player" to PlayerMap.getPlayer(uuid).name))
        } else {
            zPlayer.setIgnored(uuid, true)
            sendMessage(event.sender, format("ignoringPlayer",
                "player" to PlayerMap.getPlayer(uuid).name))
        }
    }
}