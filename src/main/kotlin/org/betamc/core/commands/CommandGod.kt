package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
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
            val onlinePlayer = Utils.getPlayerFromUsername(event.args[0])
            if (onlinePlayer == null) {
                sendMessage(event.sender, Language.PLAYER_NOT_FOUND.msg
                    .replace("%player%", event.args[0]))
                return
            }
            bmcPlayer = PlayerMap.getPlayer(onlinePlayer)
        }

        val isSelf = player.uniqueId == bmcPlayer.getUUID()
        if (!isSelf && !hasPermission(event.sender, "bmc.god.others")) {
            sendMessage(event.sender, Language.NO_PERMISSION)
            return
        }
        bmcPlayer.setGodStatus(!bmcPlayer.getGodStatus())

        val msg = if (bmcPlayer.getGodStatus()) Language.GOD_ENABLE.msg else Language.GOD_DISABLE.msg
        sendMessage(event.sender, msg
            .replace("%player%", if (isSelf) "your" else "${bmcPlayer.getName()}'s"))
        if (!isSelf) sendMessage(bmcPlayer.getOnlinePlayer(), msg
            .replace("%player%", "your"))
    }
}