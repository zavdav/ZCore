package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.*

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
                assert(zPlayer.mails.isNotEmpty(), "noMail")
                player.sendTl("mailRead")
                for (mail in zPlayer.mails) {
                    player.sendMessage(mail)
                }
            }
            "send" -> {
                if (event.args.size < 3) throw InvalidUsageException(this)

                val uuid = Utils.getUUIDFromUsername(event.args[1])
                val zTarget = PlayerMap.getPlayer(uuid)
                player.sendTl("mailSent", "name" to zTarget.name)

                if (player.uniqueId !in zTarget.ignores ||
                    hasPermission(player, "zcore.ignore.exempt")) {
                    zTarget.addMail(player.name, joinArgs(event.args, 2))
                    if (zTarget.isOnline) zTarget.onlinePlayer.sendTl("newMail")
                }
            }
            "clear" -> {
                zPlayer.clearMail()
                player.sendTl("mailCleared")
            }
            else -> throw InvalidUsageException(this)
        }
    }
}