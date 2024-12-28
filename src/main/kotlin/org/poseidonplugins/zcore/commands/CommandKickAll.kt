package org.poseidonplugins.zcore.commands

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.Utils.safeSubstring
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatString

class CommandKickAll : Command(
    "kickall",
    description = "Kicks all players from the server.",
    usage = "/kickall [message]",
    permission = "zcore.kickall",
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val reason = colorize(if (event.args.isNotEmpty()) joinArgs(event.args, 0)
            else Config.getString("defaultKickReason"))

        for (player in Bukkit.getOnlinePlayers()) {
            if (event.sender !is Player || !player.equals(event.sender as Player)) {
                player.kickPlayer(formatString(Config.getString("kickFormat"),
                    "reason" to reason).safeSubstring(0, 99))
            }
        }
        sendMessage(event.sender, format("allKicked", "reason" to reason))
    }
}