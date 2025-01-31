package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Delay
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.isSelf
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandAFK : ZCoreCommand(
    "afk",
    description = "Marks you as away-from-keyboard.",
    usage = "/afk [player]",
    permission = "zcore.afk",
    isPlayerOnly = true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        var target = event.sender as Player
        if (event.args.isNotEmpty()) {
            target = Utils.getPlayerFromUsername(event.args[0])
        }

        val isSelf = (event.sender as Player).isSelf(target)
        assert(isSelf || hasPermission(event.sender, "zcore.afk.others"), "noPermission")
        val user = User.from(target)

        if (user.isAfk) {
            user.updateActivity()
        } else if (Config.protectAfkPlayers && Config.afkDelay > 0) {
            val delay = Config.afkDelay
            target.sendTl("commencingAfk", "delay" to delay)
            target.sendTl("doNotMove")

            Delay(target, delay) { if (!user.isAfk) user.setInactive() }
        } else {
            user.setInactive()
        }
    }
}