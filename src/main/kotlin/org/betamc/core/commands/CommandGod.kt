package org.betamc.core.commands

import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
import org.betamc.core.util.format
import org.betamc.core.util.formatError
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage

class CommandGod : Command(
    "god",
    listOf("godmode"),
    "Enables god mode.",
    "/god [player]",
    "bmc.god",
    true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var bmcPlayer = PlayerMap.getPlayer(player)

        if (event.args.isNotEmpty()) {
            val target = Utils.getPlayerFromUsername(event.args[0])
            if (target == null) {
                sendMessage(event.sender, formatError("playerNotFound",
                    "player" to event.args[0]))
                return
            }
            bmcPlayer = PlayerMap.getPlayer(target)
        }

        val isSelf = player.uniqueId == bmcPlayer.uuid
        if (!isSelf && !hasPermission(event.sender, "bmc.god.others")) {
            sendMessage(event.sender, format("noPermission"))
            return
        }
        bmcPlayer.isGod = !bmcPlayer.isGod

        if (isSelf) {
            sendMessage(event.sender, if (bmcPlayer.isGod)
                format("godEnabled") else format("godDisabled"))
        } else {
            sendMessage(event.sender, if (bmcPlayer.isGod)
                format("godEnabledOther", "player" to bmcPlayer.name)
                else format("godDisabledOther", "player" to bmcPlayer.name))

            sendMessage(bmcPlayer.onlinePlayer, if (bmcPlayer.isGod)
                format("godEnabled") else format("godDisabled"))
        }
    }
}