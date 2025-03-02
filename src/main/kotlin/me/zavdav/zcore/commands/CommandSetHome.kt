package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
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
            assert(args[0].matches("^[a-zA-Z0-9_-]+$".toRegex()), "invalidHomeName")
            homeName = args[0]
        }

        val player = sender as Player
        val user = User.from(player)
        val limit = Config.multipleHomes
        val homeCount = user.getHomes().size

        if (!sender.isAuthorized("zcore.sethome.unlimited")) {
            if (!sender.isAuthorized("zcore.sethome.multiple")) {
                assert(homeCount == 0, "homeLimit", "amount" to 1)
            } else {
                assert(homeCount < limit, "homeLimit", "amount" to limit)
            }
        }

        assert(!user.homeExists(homeName), "homeAlreadyExists", "home" to homeName)
        charge(player)
        user.addHome(homeName, sender.location)
        sender.sendTl("setHome", "home" to homeName)
    }
}