package me.zavdav.zcore.commands

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission

class CommandSetHome : ZCoreCommand(
    "sethome",
    listOf("sh"),
    "Sets a home at your current location.",
    "/sethome <name>",
    "zcore.sethome",
    true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        var homeName = "main"
        if (event.args.isNotEmpty()) {
            assert(event.args[0].matches("^[a-zA-Z0-9_-]+$".toRegex()), "invalidHomeName")
            homeName = event.args[0]
        }

        val player = event.sender as Player
        val user = User.from(player)
        val limit = Config.multipleHomes
        val homeCount = user.getHomes().size

        if (!hasPermission(event.sender, "zcore.sethome.unlimited")) {
            if (!hasPermission(event.sender, "zcore.sethome.multiple")) {
                assert(homeCount == 0, "homeLimit", "amount" to 1)
            } else {
                assert(homeCount < limit, "homeLimit", "amount" to limit)
            }
        }

        assert(!user.homeExists(homeName), "homeAlreadyExists", "home" to homeName)
        charge(player)
        user.addHome(homeName, (event.sender as Player).location)
        event.sender.sendTl("setHome", "home" to homeName)
    }
}