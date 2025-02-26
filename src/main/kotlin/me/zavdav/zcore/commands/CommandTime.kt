package me.zavdav.zcore.commands

import me.zavdav.zcore.util.InvalidUsageException
import me.zavdav.zcore.util.TimeTickParser
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandTime : ZCoreCommand(
    "time",
    description = "Changes the world time.",
    usage = "/time [day|night|8:00|2pm|6000ticks] [world]",
    permission = "zcore.time",
    isPlayerOnly = true,
    maxArgs = 2
) {

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            for (world in Bukkit.getWorlds()) {
                event.sender.sendTl("currentTime",
                    "world" to world.name,
                    "time24" to TimeTickParser.format24(world.time),
                    "time12" to TimeTickParser.format12(world.time),
                    "ticks" to TimeTickParser.formatTicks(world.time))
            }
            return
        }

        var world = (event.sender as Player).world
        if (event.args.size == 2) {
            world = Bukkit.getWorld(event.args[1])
            assert(world != null, "worldNotFound", "world" to event.args[1])
        }

        val ticks = try {
            TimeTickParser.parse(event.args[0])
        } catch (_: NumberFormatException) {
            throw InvalidUsageException(this)
        }

        world.time = ticks
        event.sender.sendTl("setTime",
            "time24" to TimeTickParser.format24(ticks),
            "time12" to TimeTickParser.format12(ticks),
            "ticks" to TimeTickParser.formatTicks(ticks),
            "world" to world.name)
    }
}