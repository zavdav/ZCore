package org.betamc.core

import org.betamc.core.commands.*
import org.betamc.core.config.Language
import org.betamc.core.config.Property
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.config.Configuration
import org.poseidonplugins.commandapi.CommandManager
import java.io.File
import java.util.logging.Logger

class BMCCore : JavaPlugin() {

    private val prefix = "[BMC-Core]"
    private lateinit var logger: Logger

    private lateinit var config: Configuration
    private lateinit var language: Configuration

    override fun onEnable() {
        if (!dataFolder.exists()) dataFolder.mkdirs()
        logger = Bukkit.getLogger()
        config = Configuration(File(dataFolder, "config.yml"))
        language = Configuration(File(dataFolder, "language.yml"))
        reloadConfig()

        CommandManager(this).registerCommands(CommandBroadcast(), CommandHelp(), CommandList())
        logger.info("$prefix Has loaded, Version: ${description.version}")
    }

    override fun onDisable() {
        logger.info("$prefix Stopping plugin")
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