package me.zavdav.zcore.commands.core

import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class AbstractCommand(
    val name: String,
    val description: String,
    val syntax: String,
    val permission: String,
    val playerOnly: Boolean = true,
    val minArgs: Int = 0,
    val maxArgs: Int = -1,
    val aliases: List<String> = emptyList()
) : CommandExecutor {

    companion object {
        fun fromBukkitCommand(command: Command): AbstractCommand =
            object : AbstractCommand(
                command.name,
                command.description,
                command.usage,
                command.permission ?: "",
                false,
                aliases = command.aliases
            ) {
                override fun execute(sender: CommandSender, args: List<String>) {}
            }
    }

    private val _subcommands: MutableList<AbstractCommand> = mutableListOf()

    val subcommands: List<AbstractCommand>
        get() = _subcommands.toList()

    val labels: List<String>
        get() = listOf(name.split(" ").last(), *aliases.toTypedArray())

    abstract fun execute(sender: CommandSender, args: List<String>)

    final override fun onCommand(sender: CommandSender, command: Command,
                                 label: String, args: Array<out String>): Boolean
    {
        CommandInvoker.invokeCommand(this, sender, args.toList())
        return true
    }

    fun hasSubcommand(name: String): Boolean =
        _subcommands.any { it.name == "${this.name} $name" }

    fun addSubcommand(command: AbstractCommand) = _subcommands.add(command)

    fun getSubcommand(name: String): AbstractCommand? =
        _subcommands.firstOrNull { it.name == "${this.name} $name" }

    protected open fun charge(player: Player) {
        val cost = Config.getCommandCost(this)
        if (cost > 0.0 && !player.isAuthorized("$permission.charge.bypass")) {
            Economy.subtractBalance(player.uniqueId, cost)
            player.sendTl("commandCharge", Economy.formatBalance(cost), name)
        }
    }
}