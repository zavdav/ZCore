package me.zavdav.zcore.commands

import me.zavdav.zcore.util.CommandException
import me.zavdav.zcore.util.InvalidUsageException
import me.zavdav.zcore.util.assert
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.Preprocessor
import org.poseidonplugins.commandapi.hasPermission

class Preprocessor : Preprocessor() {
    override fun preprocess(event: CommandEvent) {
        try {
            assert(hasPermission(event.sender, event.command.permission), "noPermission")
            assert(event.sender is Player || !event.command.isPlayerOnly, "playerOnly")
            assert(event.args.size >= event.command.minArgs &&
                  (event.args.size <= event.command.maxArgs ||
                  event.command.maxArgs < 0), InvalidUsageException(event.command))

            event.command.execute(event)
        } catch (e: CommandException) {
            for (message in e.messages) {
                event.sender.sendMessage(message)
            }
        }
    }
}