package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.SpawnData
import org.poseidonplugins.zcore.util.sendTl

class CommandSetSpawn : ZCoreCommand(
    "setspawn",
    description = "Sets the world spawn to your current location.",
    usage = "/setspawn [none]",
    permission = "zcore.setspawn",
    isPlayerOnly = true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val loc = player.location
        if (event.args.size == 1 && event.args[0].equals("none", true)) {
            SpawnData.removeSpawn(loc.world.name)
            player.sendTl("resetSpawn", "world" to loc.world.name)

        } else {
            SpawnData.setSpawn(loc.world.name, loc)
            event.sender.sendTl("setSpawn",
                "world" to loc.world.name,
                "coordinates" to "${loc.blockX}, ${loc.blockY}, ${loc.blockZ}")
        }
    }
}