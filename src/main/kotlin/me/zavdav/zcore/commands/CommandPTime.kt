package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.InvalidSyntaxException
import me.zavdav.zcore.util.TimeTickParser
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandPTime : AbstractCommand(
    "ptime",
    "Changes your player time.",
    "/ptime [day|night|8:00|@2pm|@6000ticks|reset]",
    "zcore.ptime",
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        if (args.isEmpty()) {
            sender.sendTl("currentPlayerTime",
                "time24" to TimeTickParser.format24(player.playerTime),
                "time12" to TimeTickParser.format12(player.playerTime),
                "ticks" to TimeTickParser.formatTicks(player.playerTime))
            return
        }

        var string = args[0]
        val relative = if (string.startsWith("@")) {
            string = string.substring(1)
            false
        }
        else true

        val ticks = try {
            TimeTickParser.parse(string)
        } catch (_: NumberFormatException) {
            if (string.equals("reset", true)) {
                player.resetPlayerTime()
                sender.sendTl("resetPlayerTime")
                return
            }
            throw InvalidSyntaxException(this)
        }

        if (relative) {
            player.setPlayerTime(ticks + 24000 - player.world.time, true)
        } else {
            player.setPlayerTime(ticks, false)
        }

        sender.sendTl("setPlayerTime",
            "time24" to TimeTickParser.format24(ticks),
            "time12" to TimeTickParser.format12(ticks),
            "ticks" to TimeTickParser.formatTicks(ticks))
    }
}