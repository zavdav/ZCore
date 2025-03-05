package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.InvalidSyntaxException
import me.zavdav.zcore.util.TimeTickParser
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTime : AbstractCommand(
    "time",
    "Changes the world time.",
    "/time [day|night|8:00|2pm|6000ticks] [world]",
    "zcore.time",
    maxArgs = 2
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            for (world in Bukkit.getWorlds()) {
                sender.sendTl("currentTime", world.name,
                    TimeTickParser.format24(world.time),
                    TimeTickParser.format12(world.time),
                    TimeTickParser.formatTicks(world.time))
            }
            return
        }

        var world = (sender as Player).world
        if (args.size == 2) {
            world = Bukkit.getWorld(args[1])
            assert(world != null, "worldNotFound", args[1])
        }

        val ticks = try {
            TimeTickParser.parse(args[0])
        } catch (_: NumberFormatException) {
            throw InvalidSyntaxException(this)
        }

        world.time = ticks
        sender.sendTl("setTime", world.name,
            TimeTickParser.format24(ticks),
            TimeTickParser.format12(ticks),
            TimeTickParser.formatTicks(ticks))
    }
}