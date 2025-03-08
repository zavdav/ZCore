package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetHome : AbstractCommand(
    "sethome",
    "Sets a home at your current location.",
    "/sethome <name>",
    "zcore.sethome",
    maxArgs = 1,
    aliases = listOf("sh")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        var homeName = "main"
        if (args.isNotEmpty()) {
            sender.assertOrSend("invalidHomeName") { args[0].matches("^[a-zA-Z0-9_-]+$".toRegex()) }
            homeName = args[0]
        }

        val player = sender as Player
        val user = User.from(player)
        val homeCount = user.getHomes().size

        if (!sender.isAuthorized("zcore.sethome.unlimited")) {
            if (!sender.isAuthorized("zcore.sethome.multiple")) {
                sender.assertOrSend("homeLimit", 1) { homeCount == 0 }
            } else {
                val limit = Config.multipleHomes
                sender.assertOrSend("homeLimit", limit) { homeCount < limit }
            }
        }

        sender.assertOrSend("homeAlreadyExists", homeName) { !user.homeExists(homeName) }
        charge(player)
        user.addHome(homeName, sender.location)
        sender.sendTl("setHome", homeName)
    }
}