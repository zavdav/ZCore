package me.zavdav.zcore.commands

import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission

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

        val duration = formatDuration(user.playTime)
        if (isSelf) {
            event.sender.sendTl("playTime", "time" to duration)
        } else {
            event.sender.sendTl("playTimeOther", "name" to user.name, "time" to duration)
        }
    }
}