package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format

class CommandGod : Command(
    "god",
    listOf("godmode"),
    "Enables god mode.",
    "/god [player]",
    "zcore.god",
    true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var zPlayer = PlayerMap.getPlayer(player)

        if (event.args.isNotEmpty()) {
            val target = Utils.getPlayerFromUsername(event.args[0])
            zPlayer = PlayerMap.getPlayer(target)
        }

        val isSelf = player.uniqueId == zPlayer.uuid
        if (!isSelf && !hasPermission(event.sender, "zcore.god.others")) {
            event.sender.sendMessage(format("noPermission"))
            return
        }

        zPlayer.isGod = !zPlayer.isGod
        if (!isSelf) {
            event.sender.sendMessage(if (zPlayer.isGod)
                format("godEnabledOther", "player" to zPlayer.name)
            else format("godDisabledOther", "player" to zPlayer.name))
        }
        zPlayer.onlinePlayer.sendMessage(if (zPlayer.isGod)
            format("godEnabled") else format("godDisabled"))
    }
}