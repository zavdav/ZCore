package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.formatDuration
import me.zavdav.zcore.util.getUUIDFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandPlayTime : AbstractCommand(
    "playtime",
    "Shows your total playtime.",
    "/playtime [player]",
    "zcore.playtime",
    maxArgs = 1,
    aliases = listOf("pt")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        var uuid = player.uniqueId
        if (args.isNotEmpty()) {
            uuid = getUUIDFromUsername(args[0])
        }

        val isSelf = player.uniqueId == uuid
        sender.assertOrSend("noPermission") { isSelf || it.isAuthorized("zcore.playtime.others") }
        val user = User.from(uuid)
        if (user.isOnline) user.updatePlayTime()

        val duration = formatDuration(user.playTime)
        if (isSelf) {
            sender.sendTl("playTime", duration)
        } else {
            sender.sendTl("playTimeOther", user.name, duration)
        }
    }
}