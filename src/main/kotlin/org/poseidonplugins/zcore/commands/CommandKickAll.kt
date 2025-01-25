package org.poseidonplugins.zcore.commands

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.kick
import org.poseidonplugins.zcore.util.sendTl

class CommandKickAll : ZCoreCommand(
    "kickall",
    description = "Kicks all players from the server.",
    usage = "/kickall [message]",
    permission = "zcore.kickall"
) {

    override fun execute(event: CommandEvent) {
        val reason = colorize(if (event.args.isNotEmpty()) joinArgs(event.args, 0)
            else Config.getString("defaultKickReason"))

        for (player in Bukkit.getOnlinePlayers()) {
            if (event.sender !is Player || !player.equals(event.sender as Player)) {
                player.kick("kickFormat", "reason" to reason)
            }
        }
        event.sender.sendTl("allKicked", "reason" to reason)
    }
}