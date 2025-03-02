package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getUUIDFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandHome : AbstractCommand(
    "home",
    "Teleports you to the specified home.",
    "/home <name>",
    "zcore.home",
    minArgs = 1,
    maxArgs = 1,
    aliases = listOf("h")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var user = User.from(player)
        var homeName = args[0]

        if (":" in homeName) {
            assert(sender.isAuthorized("zcore.home.others"), "noPermission")
            val strings = args[0].split(":", limit = 2)
            assert(strings[1].isNotEmpty(), "noHomeSpecified")

            val uuid = getUUIDFromUsername(strings[0])
            user = User.from(uuid)
            homeName = strings[1]
        }

        assert(user.homeExists(homeName), "homeNotFound", "home" to homeName)
        val location = user.getHomeLocation(homeName)
        val isSelf = player.uniqueId == user.uuid
        if (isSelf) charge(player)
        player.teleport(location)

        homeName = user.getHomeName(homeName)
        if (isSelf) {
            sender.sendTl("teleportedToHome", "home" to homeName)
        } else {
            sender.sendTl("teleportedToHomeOther", "user" to user.name, "home" to homeName)
        }
    }
}