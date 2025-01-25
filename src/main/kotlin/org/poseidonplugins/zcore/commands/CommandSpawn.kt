package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.SpawnData
import org.poseidonplugins.zcore.util.*

class CommandSpawn : ZCoreCommand(
    "spawn",
    description = "Teleports you to the server spawn.",
    usage = "/spawn",
    permission = "zcore.spawn",
    isPlayerOnly = true,
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var loc = player.world.spawnLocation
        loc.x = loc.blockX + 0.5
        loc.z = loc.blockZ + 0.5
        loc = SpawnData.getSpawn(player.world) ?: loc
        loc.y = Utils.getSafeHeight(loc).toDouble()

        charge(player)
        player.teleport(loc)
        player.sendTl("teleportedToSpawn", "world" to loc.world.name)
    }
}