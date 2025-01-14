package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.*

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
        assert(zPlayer.uuid != uuid, "cannotIgnoreSelf")

        if (uuid in zPlayer.ignores) {
            zPlayer.setIgnored(uuid, false)
            event.sender.sendTl("notIgnoringPlayer", "name" to PlayerMap.getPlayer(uuid).name)
        } else {
            zPlayer.setIgnored(uuid, true)
            event.sender.sendTl("ignoringPlayer", "name" to PlayerMap.getPlayer(uuid).name)
        }
    }
}