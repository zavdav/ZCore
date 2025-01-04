package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.SpawnData
import org.poseidonplugins.zcore.exceptions.UnsafeDestinationException
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError

class CommandSpawn : Command(
    "spawn",
    description = "Teleports you to the server spawn.",
    usage = "/spawn",
    permission = "zcore.spawn",
    isPlayerOnly = true,
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var loc = player.world.spawnLocation
        loc.x = loc.blockX + 0.5
        loc.z = loc.blockZ + 0.5
        loc = SpawnData.getSpawn(player.world) ?: loc
        try {
            loc.y = Utils.getSafeHeight(loc).toDouble()
        } catch (e: UnsafeDestinationException) {
            event.sender.sendMessage(formatError("unsafeDestination"))
            return
        }
        player.teleport(loc)
        player.sendMessage(format("teleportedToSpawn",
            "world" to loc.world.name))
    }
}