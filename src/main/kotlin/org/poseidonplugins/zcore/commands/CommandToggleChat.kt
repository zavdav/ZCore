package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandToggleChat : Command(
    "togglechat",
    listOf("tc"),
    "Toggles whether or not you see public chat.",
    "/togglechat [player]",
    "zcore.togglechat",
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
        assert(isSelf || hasPermission(event.sender, "zcore.togglechat.others"), "noPermission")
        zPlayer.seesChat = !zPlayer.seesChat

        if (!isSelf) {
            event.sender.sendTl(if (zPlayer.seesChat)
                "chatEnabledOther" else "chatDisabledOther", zPlayer.onlinePlayer)
        }
        zPlayer.onlinePlayer.sendTl(if (zPlayer.seesChat) "chatEnabled" else "chatDisabled")
    }
}