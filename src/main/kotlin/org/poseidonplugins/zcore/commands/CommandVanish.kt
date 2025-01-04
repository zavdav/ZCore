package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format

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
        if (!isSelf && !hasPermission(event.sender, "zcore.vanish.others")) {
            event.sender.sendMessage(format("noPermission"))
            return
        }
        zPlayer.vanished = !zPlayer.vanished
        Utils.updateVanishedPlayers()

        if (!isSelf) {
            event.sender.sendMessage(if (zPlayer.vanished)
                format("vanishEnabledOther", "player" to zPlayer.name)
            else format("vanishDisabledOther", "player" to zPlayer.name))
        }
        zPlayer.onlinePlayer.sendMessage(if (zPlayer.vanished)
            format("vanishEnabled") else format("vanishDisabled"))
    }
}