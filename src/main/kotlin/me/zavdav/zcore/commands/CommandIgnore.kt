package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getUUIDFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandIgnore : AbstractCommand(
    "ignore",
    "Toggles whether or not you ignore a player.",
    "/ignore <player>",
    "zcore.ignore",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val user = User.from(sender as Player)
        val uuid = getUUIDFromUsername(args[0])
        sender.assertOrSend("cannotIgnoreSelf") { user.uuid != uuid }

        if (uuid in user.ignores) {
            user.setIgnored(uuid, false)
            sender.sendTl("unignoredPlayer", User.from(uuid).name)
        } else {
            user.setIgnored(uuid, true)
            sender.sendTl("ignoredPlayer", User.from(uuid).name)
        }
    }
}