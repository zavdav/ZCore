package me.zavdav.zcore.commands

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.format
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandMotd : ZCoreCommand(
    "motd",
    description = "Shows the message of the day.",
    usage = "/motd",
    permission = "zcore.motd",
    isPlayerOnly = true,
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        assert(Config.motd.isNotEmpty(), "noMotd")
        val player = event.sender as Player
        val motd = Config.motd.toMutableList()
        for (i in motd.indices) {
            motd[i] = format(motd[i], player, "playerlist" to Bukkit.getOnlinePlayers()
                .map { it.displayName }.sorted().joinToString(", "))

            player.sendMessage(motd[i])
        }
    }
}