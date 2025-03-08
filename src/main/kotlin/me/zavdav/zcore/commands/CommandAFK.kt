package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Delay
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandAFK : AbstractCommand(
    "afk",
    "Marks you as away-from-keyboard.",
    "/afk [player]",
    "zcore.afk",
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        var target = sender as Player
        if (args.isNotEmpty()) {
            target = getPlayerFromUsername(args[0])
        }

        sender.assertOrSend("noPermission") { it == target || it.isAuthorized("zcore.afk.others") }
        val user = User.from(target)

        if (user.isAfk) {
            user.updateActivity()
        } else if (Config.protectAfkPlayers && Config.afkDelay > 0) {
            val delay = Config.afkDelay
            target.sendTl("commencingAfk", delay)
            target.sendTl("doNotMove")

            Delay(sender, target, delay) { if (!user.isAfk) user.setInactive() }
        } else {
            user.setInactive()
        }
    }
}