package me.zavdav.zcore.commands

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.InvalidUsageException
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.send
import me.zavdav.zcore.util.sendTl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.joinArgs

class CommandMail : ZCoreCommand(
    "mail",
    description = "Manages your mails.",
    usage = "/mail <read|send <player> <message>|clear>",
    permission = "zcore.mail",
    isPlayerOnly = true,
    minArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val user = User.from(player)

        when (event.args[0].lowercase()) {
            "read" -> {
                assert(user.mails.isNotEmpty(), "noMail")
                player.sendTl("readMail")
                for (mail in user.mails) {
                    val fromUser = User.from(mail.first)
                    player.send(Config.mail,
                        "name" to fromUser.name,
                        "displayname" to fromUser.getDisplayName(false),
                        "message" to mail.second)
                }
            }
            "send" -> {
                if (user.checkIsMuted()) return
                if (event.args.size < 3) throw InvalidUsageException(this)

                val uuid = Utils.getUUIDFromUsername(event.args[1])
                val targetUser = User.from(uuid)
                player.sendTl("sentMail", "name" to targetUser.name)

                if (player.uniqueId !in targetUser.ignores ||
                    hasPermission(player, "zcore.ignore.exempt")) {
                    targetUser.addMail(player.uniqueId, joinArgs(event.args, 2))
                    if (targetUser.isOnline) targetUser.player.sendTl("newMail")
                }

                Utils.notifySocialSpy(player, event.fullCommand)
            }
            "clear" -> {
                user.clearMail()
                player.sendTl("clearedMail")
            }
            else -> throw InvalidUsageException(this)
        }
    }
}