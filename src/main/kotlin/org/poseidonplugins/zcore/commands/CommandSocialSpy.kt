package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.sendTl

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
        user.player.sendTl(if (user.socialSpy) "socialSpyEnabled" else "socialSpyDisabled")
    }
}