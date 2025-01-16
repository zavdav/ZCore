package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.util.Backup

class CommandBackup : Command(
    "zcore.backup",
    description = "Creates a new backup of ZCore's data.",
    usage = "/zcore backup",
    permission = "zcore.backup",
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        Backup.run(event.sender)
    }
}