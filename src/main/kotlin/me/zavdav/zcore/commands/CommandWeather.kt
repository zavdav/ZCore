package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.CommandSyntaxException
import me.zavdav.zcore.util.sendTl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

class CommandWeather : AbstractCommand(
    "weather",
    "Changes the world's weather.",
    "/weather <clear|rain|thunder>",
    "zcore.weather",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val world = Bukkit.getWorlds()[0]
        when (args[0].lowercase()) {
            "clear", "sun" -> {
                if (world.hasStorm()) {
                    world.isThundering = false
                    world.weatherDuration = 1
                }
                sender.sendTl("clearWeather")
            }
            "rain" -> {
                if (!world.hasStorm()) world.weatherDuration = 1
                world.isThundering = false
                sender.sendTl("rainWeather")
            }
            "thunder", "storm" -> {
                if (!world.hasStorm()) world.weatherDuration = 1
                world.isThundering = true
                sender.sendTl("thunderWeather")
            }
            else -> throw CommandSyntaxException(sender, this)
        }
    }
}