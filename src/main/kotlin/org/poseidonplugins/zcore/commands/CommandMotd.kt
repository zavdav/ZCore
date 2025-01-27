package org.poseidonplugins.zcore.commands

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.formatString

class CommandMotd : ZCoreCommand(
    "motd",
    description = "Shows the message of the day.",
    usage = "/motd",
    permission = "zcore.motd",
    isPlayerOnly = true,
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        assert(Config.motd.isNotEmpty(), "noMotdSet")
        val player = event.sender as Player
        val motd = Config.motd.toMutableList()
        for (i in motd.indices) {
            motd[i] = formatString(motd[i],
                "name" to player.name,
                "displayname" to player.displayName,
                "playerlist" to Bukkit.getOnlinePlayers()
                    .map { p: Player -> p.displayName }.sorted().joinToString(", "))

            player.sendMessage(motd[i])
        }
    }
}