package org.poseidonplugins.zcore.commands

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.formatError
import org.poseidonplugins.zcore.util.formatString

class CommandMotd : Command(
    "motd",
    description = "Shows the message of the day.",
    usage = "/motd",
    permission = "zcore.motd",
    isPlayerOnly = true,
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        if (Config.isEmpty("motd")) {
            event.sender.sendMessage(formatError("noMotdSet"))
            return
        }
        val player = event.sender as Player
        val motd = Config.getList("motd").toMutableList()
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