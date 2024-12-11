package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.player.PlayerMap
import org.betamc.core.util.Utils
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage

class CommandVanish : Command(
    "vanish",
    description = "Vanishes you or a player from other players.",
    usage = "/vanish [player]",
    permission = "bmc.vanish",
    isPlayerOnly = true,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var bmcPlayer = PlayerMap.getPlayer(player)

        if (event.args.isNotEmpty()) {
            val target = Utils.getPlayerFromUsername(event.args[0])
            if (target == null) {
                sendMessage(event.sender, Utils.format(Language.PLAYER_NOT_FOUND, event.args[0]))
                return
            }
            bmcPlayer = PlayerMap.getPlayer(target)
        }

        val isSelf = player.uniqueId == bmcPlayer.uuid
        if (!isSelf && !hasPermission(event.sender, "bmc.vanish.others")) {
            sendMessage(event.sender, Language.NO_PERMISSION)
            return
        }
        bmcPlayer.vanished = !bmcPlayer.vanished
        Utils.updateVanishedPlayers()

        sendMessage(event.sender, Utils.format(Language.VANISH_TOGGLE,
            if (isSelf) "You have" else "${bmcPlayer.name} has",
            if (bmcPlayer.vanished) "vanished" else "unvanished"))
        if (!isSelf) sendMessage(bmcPlayer.onlinePlayer, Utils.format(Language.VANISH_TOGGLE,
            "You have", if (bmcPlayer.vanished) "vanished" else "unvanished"))
    }
}