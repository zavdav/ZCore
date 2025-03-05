package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.format
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandMotd : AbstractCommand(
    "motd",
    "Shows the message of the day.",
    "/motd",
    "zcore.motd",
    maxArgs = 0
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        assert(Config.motd.isNotEmpty(), "noMotd")
        val player = sender as Player
        val motd = Config.motd.toMutableList()
        for (i in motd.indices) {
            motd[i] = format(motd[i],
                "name" to player.name, "displayname" to player.displayName,
                "list" to Bukkit.getOnlinePlayers().map { it.displayName }.sorted().joinToString(", "))

            player.sendMessage(motd[i])
        }
    }
}