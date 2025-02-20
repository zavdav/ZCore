package me.zavdav.zcore.commands

import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent

class CommandSocialSpy: ZCoreCommand(
    "socialspy",
    description = "Toggles your SocialSpy status.",
    usage = "/socialspy",
    permission = "zcore.socialspy",
    isPlayerOnly = true,
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        val user = User.from(event.sender as Player)

        user.socialSpy = !user.socialSpy
        user.player.sendTl(if (user.socialSpy) "enabledSocialSpy" else "disabledSocialSpy")
    }
}