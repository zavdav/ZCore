package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.data.SpawnData
import org.bukkit.Location
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage

class CommandSpawn : Command(
    "spawn",
    description = "Teleports you to the server spawn.",
    usage = "/spawn",
    permission = "bmc.spawn",
    isPlayerOnly = true,
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val loc = player.world.spawnLocation
        player.teleport(SpawnData.getSpawn(player.world) ?:
            Location(loc.world, loc.blockX + 0.5, loc.y, loc.blockZ + 0.5, loc.yaw, loc.pitch))
        sendMessage(player, Language.SPAWN_SUCCESS)
    }
}