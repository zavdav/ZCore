package org.poseidonplugins.zcore.commands

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.Preprocessor
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.util.format

class Preprocessor : Preprocessor() {
    override fun preprocess(event: CommandEvent) {
        if (!hasPermission(event.sender, event.command.permission)) {
            sendMessage(event.sender, format("noPermission"))
        } else if (event.command.isPlayerOnly && event.sender !is Player) {
            sendMessage(event.sender, format("playerOnly"))
        } else if (event.args.size < event.command.minArgs || (event.args.size > event.command.maxArgs && event.command.maxArgs >= 0)) {
            sendMessage(event.sender, event.command.description)
            sendMessage(event.sender, "Usage: ${event.command.usage}")
        } else {
            Bukkit.getPluginManager().callEvent(event)
            if (!event.isCancelled) event.command.execute(event)
        }
    }
}