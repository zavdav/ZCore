package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.zcore.exceptions.InvalidUsageException
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError

class CommandMail : Command(
    "mail",
    description = "Manages your mails.",
    usage = "/mail read, /mail send <player> <message>, /mail clear",
    permission = "zcore.mail",
    isPlayerOnly = true,
    minArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        val zPlayer = PlayerMap.getPlayer(player)

        when (event.args[0].lowercase()) {
            "read" -> {
                if (zPlayer.mails.isEmpty()) {
                    player.sendMessage(formatError("noMail"))
                    return
                }

                player.sendMessage(format("mailRead"))
                for (mail in zPlayer.mails) {
                    player.sendMessage(mail)
                }
            }
            "send" -> {
                if (event.args.size < 3) throw InvalidUsageException()

                val uuid = Utils.getUUIDFromUsername(event.args[1])
                val zTarget = PlayerMap.getPlayer(uuid)
                player.sendMessage(format("mailSent", "name" to zTarget.name))

                if (player.uniqueId !in zTarget.ignores ||
                    hasPermission(player, "zcore.ignore.exempt")) {
                    zTarget.addMail(player.name, joinArgs(event.args, 2))
                    if (zTarget.isOnline) zTarget.onlinePlayer.sendMessage(format("newMail"))
                }
            }
            "clear" -> {
                zPlayer.clearMail()
                player.sendMessage(format("mailCleared"))
            }
            else -> throw InvalidUsageException()
        }
    }
}