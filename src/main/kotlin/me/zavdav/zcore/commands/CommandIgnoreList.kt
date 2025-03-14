package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandIgnoreList : AbstractCommand(
    "ignorelist",
    "Shows a list of your ignored players.",
    "/ignorelist",
    "zcore.ignorelist",
    maxArgs = 0,
    aliases = listOf("ignores")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val ignores = User.from(sender as Player).ignores
        sender.assertOrSend("noIgnoredPlayers") { ignores.isNotEmpty() }

        sender.sendTl("ignoreList", ignores.joinToString(", ") { User.from(it).name })
    }
}