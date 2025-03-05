package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.CreatureType
import org.bukkit.entity.Player

class CommandSummon : AbstractCommand(
    "summon",
    "Summons the specified mob at your cursor position.",
    "/summon <mob> [amount]",
    "zcore.summon",
    minArgs = 1,
    maxArgs = 2,
    aliases = listOf("spawnmob")
) {

    val types: Map<String, CreatureType> = CreatureType.entries.associate { it.name.lowercase() to it }

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val type = args[0].lowercase()
        assert(type in types.keys, "mobNotFound", type)

        val creatureType = types[type]!!
        var amount = 1
        if (args.size == 2) {
            amount = (args[1].toIntOrNull() ?: 1).coerceIn(1..255)
        }

        val targetBlock = player.getLastTwoTargetBlocks(null, 100)[0]
        repeat(amount) {
            player.world.spawnCreature(targetBlock.location, creatureType)
        }
        player.sendTl("summonedMob", amount, creatureType.name)
    }
}