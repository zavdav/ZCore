package me.zavdav.zcore.commands

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Delay
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission

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
            target = getPlayerFromUsername(event.args[0])
        }

        assert(event.sender == target || hasPermission(event.sender, "zcore.afk.others"), "noPermission")
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