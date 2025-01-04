package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.*
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Delay
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.Utils.isSelf
import org.poseidonplugins.zcore.util.format

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
        if (!isSelf && !hasPermission(event.sender, "zcore.afk.others")) {
            event.sender.sendMessage(format("noPermission"))
            return
        }

        val zPlayer = PlayerMap.getPlayer(target)
        if (zPlayer.isAFK) {
            zPlayer.isAFK = false
            broadcastMessage(format("noLongerAfk", "player" to target.name))
        } else if (Config.getBoolean("protectAfkPlayers") && Config.getInt("afkDelay", 0) > 0) {
            val delay = Config.getInt("afkDelay", 0)
            target.sendMessage(format("commencingAfk", "delay" to delay))
            target.sendMessage(format("doNotMove"))

            Delay(target, {
                if (!zPlayer.isAFK) {
                    zPlayer.isAFK = true
                    broadcastMessage(format("nowAfk", "player" to target.name))
                }
            }, delay)
        } else {
            zPlayer.isAFK = true
            broadcastMessage(format("nowAfk", "player" to target.name))
        }
    }
}