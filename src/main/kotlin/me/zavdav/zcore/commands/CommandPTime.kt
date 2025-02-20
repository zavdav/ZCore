package me.zavdav.zcore.commands

import me.zavdav.zcore.util.InvalidUsageException
import me.zavdav.zcore.util.TimeTickParser
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandPTime : ZCoreCommand(
    "ptime",
    description = "Changes your player time.",
    usage = "/ptime [day|night|8:00|@2pm|@6000ticks|reset]",
    permission = "zcore.ptime",
    isPlayerOnly = true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        if (event.args.isEmpty()) {
            event.sender.sendTl("currentPlayerTime",
                "time24" to TimeTickParser.format24(player.playerTime),
                "time12" to TimeTickParser.format12(player.playerTime),
                "ticks" to TimeTickParser.formatTicks(player.playerTime))
            return
        }

        var string = event.args[0]
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
                event.sender.sendTl("resetPlayerTime")
                return
            }
            throw InvalidUsageException(this)
        }

        if (relative) {
            player.setPlayerTime(ticks + 24000 - player.world.time, true)
        } else {
            player.setPlayerTime(ticks, false)
        }

        event.sender.sendTl("setPlayerTime",
            "time24" to TimeTickParser.format24(ticks),
            "time12" to TimeTickParser.format12(ticks),
            "ticks" to TimeTickParser.formatTicks(ticks))
    }
}