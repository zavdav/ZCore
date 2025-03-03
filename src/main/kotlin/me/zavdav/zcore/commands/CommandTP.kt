package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.CommandException
import me.zavdav.zcore.util.getPlayerFromUsername
import me.zavdav.zcore.util.getSafeHeight
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTP : AbstractCommand(
    "tp",
    "Teleports you to a player or to coordinates.",
    "/tp [target] <player|x, y, z>",
    "zcore.tp",
    minArgs = 1,
    maxArgs = 4,
    aliases = listOf("teleport")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val player = sender as Player
        when (args.size) {
            1, 2 -> teleportPlayerToPlayer(player, args)
            3, 4 -> teleportPlayerToCoordinates(player, args)
        }
    }

    private fun teleportPlayerToPlayer(sender: Player, args: List<String>) {
        val player: Player
        val target: Player

        if (args.size == 2) {
            player = getPlayerFromUsername(args[0])
            target = getPlayerFromUsername(args[1])
        } else {
            player = sender
            target = getPlayerFromUsername(args[0])
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
            target = getPlayerFromUsername(strings[0])
            strings = strings.subList(1, strings.size)
        }

        val coords = parseCoordinates(target, strings)
        target.teleport(Location(target.world, coords[0], coords[1], coords[2], target.location.yaw, target.location.pitch))

        val coordinates = "${coords[0].toFloat()}, ${coords[1].toFloat()}, ${coords[2].toFloat()}"
        if (player == target) {
            player.sendTl("teleportedToCoords", "coordinates" to coordinates)
        } else {
            player.sendTl("teleportedPlayerToCoords",
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
            } catch (_: NumberFormatException) {
                if (args[i] == "=") {
                    coords.add(playerCoords[i])
                } else if (i == 1 && args[i] == "~") {
                    coords.add(loc.y)
                    computeY = true
                } else {
                    throw CommandException(tl(
                        "invalidCoords", "string" to args.joinToString(" ")
                    ))
                }
            }
        }

        if (computeY) coords[1] = getSafeHeight(Location(loc.world, coords[0], coords[1], coords[2])).toDouble()
        return coords.toList()
    }
}