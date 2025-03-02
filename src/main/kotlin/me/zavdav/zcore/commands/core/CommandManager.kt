package me.zavdav.zcore.commands.core

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.getField
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.Plugin

object CommandManager {

    private val commandMap: CommandMap = getField(Bukkit.getPluginManager(), "commandMap")!!
    private val commands: MutableList<AbstractCommand> = mutableListOf()
    private val subcommandQueue: MutableMap<Int, MutableList<AbstractCommand>> = mutableMapOf()

    val zcoreCommands: List<AbstractCommand>
        get() = commands.toList()

    val otherCommands: List<AbstractCommand>
        get() {
            val knownCommands = getField<Map<String, Command>>(commandMap, "knownCommands")!!
            return knownCommands
                .filter { (k, v) -> v is PluginCommand && v.plugin !is ZCore && k == v.name}
                .values
                .sortedWith(compareBy({ (it as PluginCommand).plugin.description.name }, { it.name }))
                .map { AbstractCommand.fromBukkitCommand(it) }
        }

    fun registerCommands(vararg commands: AbstractCommand) {
        for (command in commands) {
            if (" " in command.name) {
                queueSubcommand(command)
            } else {
                registerCommand(command)
            }
        }
        registerSubcommands()
    }

    fun unregisterAll() {
        commands.clear()
        val knownCommands = getField<MutableMap<String, Command>>(commandMap, "knownCommands")!!
        knownCommands.entries.removeIf { (_, it) -> it is PluginCommand && it.plugin is ZCore }
    }

    private fun registerCommand(command: AbstractCommand) {
        val constructor = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
        constructor.isAccessible = true
        val plCommand = constructor.newInstance(command.name, ZCore.INSTANCE)
        constructor.isAccessible = false

        plCommand.description = command.description
        plCommand.usage = command.syntax
        plCommand.aliases = command.aliases
        plCommand.executor = command

        commandMap.register(ZCore.INSTANCE.description.name, plCommand)
        commands.add(command)
    }

    private fun queueSubcommand(command: AbstractCommand) {
        val level = command.name.split(" ").lastIndex
        val queue = subcommandQueue[level] ?: mutableListOf()
        if (command !in queue) queue.add(command)
        subcommandQueue[level] = queue
    }

    private fun registerSubcommands() {
        if (subcommandQueue.isEmpty()) return

        for (i in 1..subcommandQueue.keys.max()) {
            val queue = subcommandQueue[i] ?: return
            for (command in queue) {
                val split = command.name.split(" ")
                val parent = getParent(split, commands.firstOrNull { it.name == split[0] }, 1) ?: continue
                registerSubcommand(parent, command)
            }
        }

        subcommandQueue.clear()
    }

    private fun registerSubcommand(parent: AbstractCommand, command: AbstractCommand) {
        if (parent.subcommands.isNotEmpty()) {
            for (alias in command.labels) {
                if (command.subcommands.any { alias in it.labels }) return
            }
        }

        parent.addSubcommand(command)
        commands.add(command)
    }

    private tailrec fun getParent(split: List<String>, subcommand: AbstractCommand?, start: Int = 1): AbstractCommand? {
        if (subcommand == null) return null
        if (start > split.size - 2) return subcommand
        return if (subcommand.hasSubcommand(split[start])) {
            getParent(split, subcommand.getSubcommand(split[start]), start + 1)
        }
        else null
    }
}