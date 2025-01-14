package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandSetHome : Command(
    "sethome",
    listOf("sh"),
    "Sets a home at your current location.",
    "/sethome <name>",
    "zcore.sethome",
    true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        var homeName = "main"
        if (event.args.isNotEmpty()) {
            assert(event.args[0].matches("^[a-zA-Z0-9_-]+$".toRegex()), "invalidHomeName")
            homeName = event.args[0]
        }

        val zPlayer = PlayerMap.getPlayer(event.sender as Player)
        val limit = Config.getInt("multipleHomes", 2)
        val homeCount = zPlayer.getHomes().size

        if (!hasPermission(event.sender, "zcore.sethome.unlimited")) {
            if (!hasPermission(event.sender, "zcore.sethome.multiple")) {
                assert(homeCount == 0, "homeLimitSingle")
            } else {
                assert(homeCount < limit, "homeLimitMultiple", "amount" to limit)
            }
        }

        assert(!zPlayer.homeExists(homeName), "homeAlreadyExists")
        zPlayer.addHome(homeName, (event.sender as Player).location)
        event.sender.sendTl("homeSet", "home" to homeName)
    }
}