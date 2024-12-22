package org.betamc.core.commands

import org.betamc.core.config.Property
import org.betamc.core.util.format
import org.betamc.core.util.formatString
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.sendMessage

class CommandMotd : Command(
    "motd",
    description = "Shows the message of the day.",
    usage = "/motd",
    permission = "bmc.motd",
    isPlayerOnly = true,
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        if (Property.MOTD.toString().isEmpty()) {
            sendMessage(event.sender, format("noMotdSet"))
            return
        }
        val player = event.sender as Player
        val motd = Property.MOTD.toList()
        for (i in motd.indices) {
            motd[i] = formatString(colorize(motd[i]),
                "username" to player.name,
                "displayname" to player.displayName,
                "list" to Bukkit.getOnlinePlayers()
                    .map { p: Player -> p.name }.sorted().joinToString(", "))

            sendMessage(player, motd[i])
        }
    }
}