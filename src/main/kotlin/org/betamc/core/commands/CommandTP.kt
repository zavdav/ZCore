package org.betamc.core.commands

import org.betamc.core.config.Language
import org.betamc.core.util.Utils
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage

class CommandTP : Command(
    "tp",
    listOf("teleport"),
    "Teleports you or a player to coordinates or another player.",
    "/tp [target] <x> <y> <z>, /tp [target] <player>",
    "bmc.tp",
    true,
    1,
    4,
    Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val player = event.sender as Player
        when (event.args.size) {
            1, 2 -> teleportPlayerToPlayer(player, event.args)
            3, 4 -> teleportPlayerToCoordinates(player, event.args)
        }
    }

    private fun teleportPlayerToPlayer(sender: Player, args: List<String>) {
        var player: Player? = sender
        var target: Player? = Utils.getPlayerFromUsername(args[0])

        if (args.size == 2) {
            player = Utils.getPlayerFromUsername(args[0])
            target = Utils.getPlayerFromUsername(args[1])
            if (player == null) {
                sendMessage(sender, Utils.format(Language.PLAYER_NOT_FOUND, args[0]))
                return
            }
        }
        if (target == null) {
            sendMessage(sender, Utils.format(Language.PLAYER_NOT_FOUND,
                if (args.size == 2) args[1] else args[0]))
            return
        }

        player!!.teleport(target)
        sendMessage(sender, Utils.format(Language.TP_SUCCESS,
            if (sender == player) "You have" else "${player.name} has",
            if (sender == target) "you" else target.name))
    }

    private fun teleportPlayerToCoordinates(player: Player, args: List<String>) {
        var strings = args
        var target: Player? = player

        if (strings.size == 4) {
            target = Utils.getPlayerFromUsername(strings[0])
            if (target == null) {
                sendMessage(player, Utils.format(Language.PLAYER_NOT_FOUND, strings[0]))
                return
            }
            strings = strings.subList(1, strings.size)
        }

        val coords = parseCoordinates(player, target!!, strings) ?: return
        target.teleport(Location(target.world, coords[0], coords[1], coords[2], target.location.yaw, target.location.pitch))
        sendMessage(player, Utils.format(Language.TP_SUCCESS,
            if (player == target) "You have" else "${target.name} has",
            "${coords[0].toFloat()}, ${coords[1].toFloat()}, ${coords[2].toFloat()}"))
    }

    private fun parseCoordinates(sender: CommandSender, player: Player, args: List<String>): List<Double>? {
        val loc = player.location
        val coords: MutableList<Double> = mutableListOf()
        val playerCoords = listOf(loc.x, loc.y, loc.z)
        var computeY = false
        for (i in args.indices) {
            try {
                val intCoord = args[i].toIntOrNull()
                val coord: Double = if (intCoord != null && i != 1) {
                    intCoord + 0.5
                } else args[i].toDouble()
                coords.add(coord)
            } catch (e: NumberFormatException) {
                when (args[i]) {
                    "=" -> coords.add(playerCoords[i])
                    else -> if (i == 1 && args[i] == "~") {
                        coords.add(loc.y)
                        computeY = true
                    } else {
                        sendMessage(sender, Utils.format(Language.TP_PARSE_ERROR,
                            args.joinToString(" ")))
                        return null
                    }
                }
            }
        }

        if (computeY) {
            try {
                coords[1] = Utils.getSafeHeight(Location(loc.world, coords[0], coords[1], coords[2])).toDouble()
            } catch (e: Exception) {
                sendMessage(sender, Language.UNSAFE_DESTINATION)
                return null
            }
        }

        return coords.toList()
    }
}