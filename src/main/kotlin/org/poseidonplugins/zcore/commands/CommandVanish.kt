package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandVanish : Command(
    "vanish",
    description = "Vanishes you or a player from other players.",
    usage = "/vanish [player]",
    permission = "zcore.vanish",
    isPlayerOnly = true,
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
        assert(isSelf || hasPermission(event.sender, "zcore.vanish.others"), "noPermission")
        zPlayer.vanished = !zPlayer.vanished
        Utils.updateVanishedPlayers()

        if (!isSelf) {
            event.sender.sendTl(if (zPlayer.vanished)
                "vanishEnabledOther" else "vanishDisabledOther", zPlayer.onlinePlayer)
        }
        zPlayer.onlinePlayer.sendTl(if (zPlayer.vanished) "vanishEnabled" else "vanishDisabled")
    }
}