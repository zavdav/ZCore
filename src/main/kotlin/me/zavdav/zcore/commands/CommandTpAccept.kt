package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Delay
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTpAccept : AbstractCommand(
    "tpaccept",
    "Accepts your current teleport request.",
    "/tpaccept",
    "zcore.tpaccept",
    maxArgs = 0,
    aliases = listOf("tpyes")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        val user = User.from(player)
        val request = user.tpRequest
        sender.assertOrSend("noTpRequest") { request != null }

        val target = request!!.first
        val requestType = request.second
        val delay = Config.teleportDelay
        user.tpRequest = null
        player.sendTl("acceptedTpRequest", target.name)
        target.sendTl("otherAcceptedRequest", player.name)

        if (requestType == User.TeleportType.TPA) {
            if (delay > 0) {
                target.sendTl("commencingTeleport", player.name, delay)
                target.sendTl("doNotMove")
            }
            Delay(sender, target, delay) {
                charge(target)
                target.teleport(player)
            }
        } else {
            if (delay > 0) {
                player.sendTl("commencingTeleport", target.name, delay)
                player.sendTl("doNotMove")
            }
            Delay(target, player, delay) {
                charge(target)
                player.teleport(target)
            }
        }
    }

    override fun charge(player: Player) {
        val cost = Config.getCommandCost(this)
        if (cost > 0.0 && !player.isAuthorized("$permission.charge.bypass")) {
            Economy.subtractBalance(player.uniqueId, cost)
            player.sendTl("tpRequestCharge", Economy.formatBalance(cost))
        }
    }
}