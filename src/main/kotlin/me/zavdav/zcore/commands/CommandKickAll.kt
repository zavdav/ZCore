package me.zavdav.zcore.commands

import me.zavdav.zcore.util.kick
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.joinArgs

class CommandKickAll : ZCoreCommand(
    "kickall",
    description = "Kicks all players from the server.",
    usage = "/kickall [message]",
    permission = "zcore.kickall"
) {

    override fun execute(event: CommandEvent) {
        val reason = colorize(if (event.args.isNotEmpty()) joinArgs(event.args, 0)
            else tl("kickReason"))

        for (player in Bukkit.getOnlinePlayers()) {
            if (event.sender !is Player || !player.equals(event.sender as Player)) {
                player.kick("kickScreen", "reason" to reason)
            }
        }
        event.sender.sendTl("kickedAll", "reason" to reason)
    }
}