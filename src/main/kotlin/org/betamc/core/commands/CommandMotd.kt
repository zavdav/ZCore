package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.config.Property
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
            sendMessage(event.sender, Language.MOTD_NOT_SET)
            return
        }
        val player = event.sender as Player
        val motd = Property.MOTD.toList()
        for (i in motd.indices) {
            motd[i] = colorize(motd[i].replace("{USERNAME}", player.name)
                .replace("{DISPLAYNAME}", player.displayName)
                .replace("{LIST}", Bukkit.getOnlinePlayers()
                        .map { p: Player -> p.name }.sorted().joinToString(", ")))

            sendMessage(player, motd[i])
        }
    }
}