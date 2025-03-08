package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.CommandSyntaxException
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.getUUIDFromUsername
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.joinArgs
import me.zavdav.zcore.util.notifySocialSpy
import me.zavdav.zcore.util.send
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandMail : AbstractCommand(
    "mail",
    "Manages your mails.",
    "/mail <read|send <player> <message>|clear>",
    "zcore.mail",
    minArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val user = User.from(player)

        when (args[0].lowercase()) {
            "read" -> {
                sender.assertOrSend("noMail") { user.mails.isNotEmpty() }
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
                if (args.size < 3) throw CommandSyntaxException(sender, this)

                val uuid = getUUIDFromUsername(args[1])
                val targetUser = User.from(uuid)
                player.sendTl("sentMail", targetUser.name)

                if (player.uniqueId !in targetUser.ignores ||
                    player.isAuthorized("zcore.ignore.exempt")) {
                    targetUser.addMail(player.uniqueId, joinArgs(args, 2))
                    if (targetUser.isOnline) targetUser.player.sendTl("newMail")
                }

                notifySocialSpy(player, "/$name ${args.joinToString(" ")}")
            }
            "clear" -> {
                user.clearMail()
                player.sendTl("clearedMail")
            }
            else -> throw CommandSyntaxException(sender, this)
        }
    }
}