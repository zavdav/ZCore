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
                sendMessage(event.sender, Utils.format(Language.PLAYER_NOT_FOUND, event.args[0]))
                return
            }
            bmcPlayer = PlayerMap.getPlayer(onlinePlayer)
        }

        val isSelf = player.uniqueId == bmcPlayer.getUUID()
        if (!isSelf && !hasPermission(event.sender, "bmc.god.others")) {
            sendMessage(event.sender, Language.NO_PERMISSION)
            return
        }
        bmcPlayer.setGodMode(!bmcPlayer.hasGodMode())

        sendMessage(event.sender, Utils.format(Language.GOD_TOGGLE,
            if (isSelf) "Your" else "${bmcPlayer.getName()}'s",
            if (bmcPlayer.hasGodMode()) "enabled" else "disabled"))
        if (!isSelf) sendMessage(bmcPlayer.getOnlinePlayer(), Utils.format(Language.GOD_TOGGLE,
            "Your", if (bmcPlayer.hasGodMode()) "enabled" else "disabled"))
    }
}