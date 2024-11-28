package org.betamc.core

import org.betamc.core.commands.*
import org.betamc.core.config.Language
import org.betamc.core.config.Property
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.util.config.Configuration
import org.poseidonplugins.commandapi.CommandManager
import java.io.File
import java.util.logging.Logger

object BMCCore {

    const val prefix = "[BMC-Core]"
    private var enabled = false

    lateinit var plugin: Plugin private set
    lateinit var dataFolder: File private set
    lateinit var logger: Logger private set
    lateinit var cmdManager: CommandManager private set

    lateinit var config: Configuration private set
    lateinit var language: Configuration private set

    fun enable(plugin: Plugin) {
        if (enabled) return
        this.plugin = plugin
        dataFolder = plugin.dataFolder
        logger = Bukkit.getLogger()

        if (!dataFolder.exists()) dataFolder.mkdirs()
        config = Configuration(File(dataFolder, "config.yml"))
        language = Configuration(File(dataFolder, "language.yml"))
        reloadConfig()

        cmdManager = CommandManager(plugin)
        cmdManager.registerCommands(
            CommandBroadcast(),
            CommandHeal(),
            CommandHelp(),
            CommandKick(),
            CommandKickAll(),
            CommandList()
        )

        enabled = true
        logger.info("$prefix ${plugin.description.name} ${plugin.description.version} has been enabled.")
    }

    fun disable() {
        if (!enabled) return
        enabled = false
        logger.info("$prefix ${plugin.description.name} ${plugin.description.version} has been disabled.")
    }

    private fun reloadConfig() {
        config.load()
        for (property in Property.entries) {
            if (config.getProperty(property.key) == null) {
                config.setProperty(property.key, property.value)
            }
            property.value = config.getProperty(property.key)
        }
        config.save()
        config.load()

        language.load()
        for (lang in Language.entries) {
            if (language.getProperty(lang.name) == null) {
                language.setProperty(lang.name, lang.msg)
            }
            lang.msg = language.getProperty(lang.name).toString()
        }
        language.save()
        language.load()
    }

}