package me.zavdav.zcore.commands

import me.zavdav.zcore.util.InvalidUsageException
import me.zavdav.zcore.util.sendTl
import org.bukkit.Bukkit
import org.poseidonplugins.commandapi.CommandEvent

class CommandWeather : ZCoreCommand(
    "weather",
    description = "Changes the world's weather.",
    usage = "/weather <clear|rain|thunder>",
    permission = "zcore.weather",
    isPlayerOnly = true,
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val world = Bukkit.getWorlds()[0]
        when (event.args[0].lowercase()) {
            "clear", "sun" -> {
                if (world.hasStorm()) {
                    world.isThundering = false
                    world.weatherDuration = 1
                }
                event.sender.sendTl("clearWeather")
            }
            "rain" -> {
                if (!world.hasStorm()) world.weatherDuration = 1
                world.isThundering = false
                event.sender.sendTl("rainWeather")
            }
            "thunder", "storm" -> {
                if (!world.hasStorm()) world.weatherDuration = 1
                world.isThundering = true
                event.sender.sendTl("thunderWeather")
            }
            else -> throw InvalidUsageException(this)
        }
    }
}