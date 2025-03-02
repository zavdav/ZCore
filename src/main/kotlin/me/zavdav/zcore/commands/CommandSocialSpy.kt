package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSocialSpy: AbstractCommand(
    "socialspy",
    "Toggles your SocialSpy status.",
    "/socialspy",
    "zcore.socialspy",
    maxArgs = 0
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val user = User.from(sender as Player)

        user.socialSpy = !user.socialSpy
        user.player.sendTl(if (user.socialSpy) "enabledSocialSpy" else "disabledSocialSpy")
    }
}