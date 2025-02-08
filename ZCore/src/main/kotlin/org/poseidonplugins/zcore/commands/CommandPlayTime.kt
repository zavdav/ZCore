package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandPlayTime : ZCoreCommand(
    "playtime",
    listOf("pt"),
    "Shows your total playtime.",
    "/playtime [player]",
    "zcore.playtime",
    true,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        var uuid = player.uniqueId
        if (event.args.isNotEmpty()) {
            uuid = Utils.getUUIDFromUsername(event.args[0])
        }

        val isSelf = player.uniqueId == uuid
        assert(isSelf || hasPermission(event.sender, "zcore.playtime.others"), "noPermission")
        val user = User.from(uuid)
        if (user.isOnline) user.updatePlayTime()

        val duration = Utils.formatDuration(user.playTime)
        if (isSelf) {
            event.sender.sendTl("playTime", "time" to duration)
        } else {
            event.sender.sendTl("playTimeOther", "name" to user.name, "time" to duration)
        }
    }
}