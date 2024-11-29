package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.player.PlayerMap
import org.bukkit.Bukkit
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
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        if (event.args.isEmpty()) {
            if (event.sender !is Player) {
                sendMessage(event.sender, Language.PLAYER_ONLY)
                return
            }

            val bmcPlayer = PlayerMap.getPlayer(event.sender as Player)
            bmcPlayer.setGodStatus(!bmcPlayer.getGodStatus())
            sendMessage(event.sender, if (bmcPlayer.getGodStatus()) Language.GOD_ENABLE_SELF else Language.GOD_DISABLE_SELF)
        } else if (hasPermission(event.sender, "bmc.god.others")) {
            val player: Player? = Bukkit.matchPlayer(event.args[0]).getOrNull(0)
            if (player == null) {
                sendMessage(event.sender, Language.PLAYER_NOT_FOUND.msg
                    .replace("%player%", event.args[0]))
                return
            }

            val bmcPlayer = PlayerMap.getPlayer(player)
            bmcPlayer.setGodStatus(!bmcPlayer.getGodStatus())
            if (!event.sender.name.equals(player.name, true)) {
                sendMessage(event.sender, if (bmcPlayer.getGodStatus())
                    Language.GOD_ENABLE_PLAYER.msg.replace("%player%", player.name)
                    else Language.GOD_DISABLE_PLAYER.msg.replace("%player%", player.name))
            }
            sendMessage(player, if (bmcPlayer.getGodStatus()) Language.GOD_ENABLE_SELF else Language.GOD_DISABLE_SELF)
        } else {
            sendMessage(event.sender, Language.NO_PERMISSION)
        }
    }
}