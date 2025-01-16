package org.poseidonplugins.zcore.commands

import org.bukkit.Location
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.util.*

class CommandTP : Command(
    "tp",
    listOf("teleport"),
    "Teleports you or a player to coordinates or another player.",
    "/tp [target] <x> <y> <z>, /tp [target] <player>",
    "zcore.tp",
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
        val player: Player
        val target: Player

        if (args.size == 2) {
            player = Utils.getPlayerFromUsername(args[0])
            target = Utils.getPlayerFromUsername(args[1])
        } else {
            player = sender
            target = Utils.getPlayerFromUsername(args[0])
        }

        player.teleport(target)
        if (sender == player) {
            sender.sendTl("teleportedToPlayer", "player" to target.name)
        } else {
            if (sender == target) {
                sender.sendTl("teleportedPlayer", "player" to player.name)
            } else {
                sender.sendTl("teleportedPlayerToPlayer", "player" to player.name, "other" to target.name)
            }
        }
    }

    private fun teleportPlayerToCoordinates(player: Player, args: List<String>) {
        var strings = args
        var target = player

        if (strings.size == 4) {
            target = Utils.getPlayerFromUsername(strings[0])
            strings = strings.subList(1, strings.size)
        }

        val coords = parseCoordinates(target, strings)
        target.teleport(Location(target.world, coords[0], coords[1], coords[2], target.location.yaw, target.location.pitch))

        val coordinates = "${coords[0].toFloat()}, ${coords[1].toFloat()}, ${coords[2].toFloat()}"
        if (player == target) {
            player.sendTl("teleportedToCoordinates", "coordinates" to coordinates)
        } else {
            player.sendTl("teleportedPlayerToCoordinates",
                "player" to target.name, "coordinates" to coordinates)
        }
    }

    private fun parseCoordinates(player: Player, args: List<String>): List<Double> {
        val loc = player.location
        val coords: MutableList<Double> = mutableListOf()
        val playerCoords = listOf(loc.x, loc.y, loc.z)
        var computeY = false

        for (i in args.indices) {
            try {
                val intCoord = args[i].toIntOrNull()
                val coord: Double =
                    if (intCoord != null && i != 1) intCoord + 0.5 else args[i].toDouble()
                coords.add(coord)
            } catch (e: NumberFormatException) {
                if (args[i] == "=") {
                    coords.add(playerCoords[i])
                } else if (i == 1 && args[i] == "~") {
                    coords.add(loc.y)
                    computeY = true
                } else {
                    throw CommandException(formatError(
                        "errorParsingCoordinates", "string" to args.joinToString(" ")
                    ))
                }
            }
        }

        if (computeY) coords[1] = Utils.getSafeHeight(Location(loc.world, coords[0], coords[1], coords[2])).toDouble()
        return coords.toList()
    }
}