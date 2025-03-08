package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getUUIDFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandDelHome : AbstractCommand(
    "delhome",
    "Deletes the specified home.",
    "/delhome <name>",
    "zcore.delhome",
    minArgs = 1,
    maxArgs = 1,
    aliases = listOf("dh")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var user = User.from(player)
        var homeName = args[0]

        if (":" in homeName) {
            sender.assertOrSend("noPermission") { it.isAuthorized("zcore.delhome.others") }
            val strings = args[0].split(":", limit = 2)
            sender.assertOrSend("noHomeSpecified") { strings[1].isNotEmpty() }

            val uuid = getUUIDFromUsername(strings[0])
            user = User.from(uuid)
            homeName = strings[1]
        }

        sender.assertOrSend("homeNotFound", homeName) { user.homeExists(homeName) }
        homeName = user.getHomeName(homeName)
        user.removeHome(homeName)

        if (player.uniqueId == user.uuid) {
            sender.sendTl("deletedHome", homeName)
        } else {
            sender.sendTl("deletedHomeOther", user.name, homeName)
        }
    }
}