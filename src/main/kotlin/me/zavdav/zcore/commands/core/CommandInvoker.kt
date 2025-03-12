package me.zavdav.zcore.commands.core

import me.zavdav.zcore.api.EconomyException
import me.zavdav.zcore.util.CommandException
import me.zavdav.zcore.util.CommandSyntaxException
import me.zavdav.zcore.util.MiscellaneousException
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.isAuthorized
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object CommandInvoker {

    fun invokeCommand(command: AbstractCommand, sender: CommandSender, args: List<String>) {
        val subcommand = if (args.isNotEmpty()) command.getSubcommand(args[0]) else null

        if (subcommand != null) {
            val newArgs = args.subList(1, args.size)
            invokeCommand(subcommand, sender, newArgs)
        } else try {
            sender.assertOrSend("noPermission") { it.isAuthorized(command.permission) }
            sender.assertOrSend("playerOnly") { it is Player || !command.playerOnly }
            if (args.size < command.minArgs ||
                args.size > command.maxArgs && command.maxArgs >= 0)
            {
                throw CommandSyntaxException(sender, command)
            }

            command.execute(sender, args)
        } catch (e: EconomyException) {
            sender.sendMessage(e.message)
        } catch (e: MiscellaneousException) {
            sender.sendMessage(e.message)
        } catch (_: CommandException) {}
    }
}