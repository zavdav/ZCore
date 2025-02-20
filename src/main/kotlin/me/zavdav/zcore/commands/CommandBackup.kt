package me.zavdav.zcore.commands

import me.zavdav.zcore.util.Backup
import org.poseidonplugins.commandapi.CommandEvent

class CommandBackup : ZCoreCommand(
    "zcore.backup",
    description = "Creates a new backup of ZCore's data.",
    usage = "/zcore backup",
    permission = "zcore.backup",
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        Backup.run(event.sender)
    }
}