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

class CommandAFK : Command(
    "afk",
    description = "Marks you as away-from-keyboard.",
    usage = "/afk [player]",
    permission = "zcore.afk",
    isPlayerOnly = true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

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
        } else if (Config.getBoolean("protectAfkPlayers") && Config.getInt("afkDelay", 0) > 0) {
            val delay = Config.getInt("afkDelay", 0)
            target.sendTl("commencingAfk", "delay" to delay)
            target.sendTl("doNotMove")

            Delay(target, { if (!user.isAfk) user.setInactive() }, delay)
        } else {
            user.setInactive()
        }
    }
}