package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.*

class CommandMail : ZCoreCommand(
    "mail",
    description = "Manages your mails.",
    usage = "/mail read, /mail send <player> <message>, /mail clear",
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
                player.sendTl("mailRead")
                for (mail in user.mails) {
                    player.sendMessage(mail)
                }
            }
            "send" -> {
                if (user.checkIsMuted()) return
                if (event.args.size < 3) throw InvalidUsageException(this)

                val uuid = Utils.getUUIDFromUsername(event.args[1])
                val targetUser = User.from(uuid)
                player.sendTl("mailSent", "name" to targetUser.name)

                if (player.uniqueId !in targetUser.ignores ||
                    hasPermission(player, "zcore.ignore.exempt")) {
                    targetUser.addMail(player.name, joinArgs(event.args, 2))
                    if (targetUser.isOnline) targetUser.player.sendTl("newMail")
                }
            }
            "clear" -> {
                user.clearMail()
                player.sendTl("mailCleared")
            }
            else -> throw InvalidUsageException(this)
        }
    }
}