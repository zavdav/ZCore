package org.betamc.core

import org.betamc.core.commands.*
import org.betamc.core.config.Language
import org.betamc.core.config.Property
import org.betamc.core.data.BanData
import org.betamc.core.data.SpawnData
import org.betamc.core.listeners.PlayerListener
import org.betamc.core.player.PlayerMap
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.config.Configuration
import org.poseidonplugins.commandapi.CommandManager
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.util.logging.Logger

class BMCCore : JavaPlugin() {

    companion object {
        const val prefix = "[BMC-Core]"

        lateinit var plugin: Plugin private set
        lateinit var dataFolder: File private set
        lateinit var logger: Logger private set
        lateinit var cmdManager: CommandManager private set

        lateinit var config: Configuration private set
        lateinit var language: Configuration private set
    }

    private var lastAutoSave: LocalDateTime = LocalDateTime.now()


    override fun onEnable() {
        plugin = this
        Companion.dataFolder = plugin.dataFolder
        logger = Bukkit.getLogger()

        if (!dataFolder.exists()) dataFolder.mkdirs()
        config = Configuration(File(dataFolder, "config.yml"))
        language = Configuration(File(dataFolder, "language.yml"))
        reloadConfig()

        cmdManager = CommandManager(plugin)
        cmdManager.registerCommands(
            CommandBan(),
            CommandBanIP(),
            CommandBMC(),
            CommandBroadcast(),
            CommandDelHome(),
            CommandGod(),
            CommandHeal(),
            CommandHelp(),
            CommandHome(),
            CommandHomes(),
            CommandInvSee(),
            CommandKick(),
            CommandKickAll(),
            CommandList(),
            CommandReload(),
            CommandSeen(),
            CommandSetHome(),
            CommandSetSpawn(),
            CommandSpawn(),
            CommandTP(),
            CommandUnban(),
            CommandUnbanIP(),
            CommandVanish()
        )

        Bukkit.getPluginManager().registerEvents(PlayerListener(), plugin)

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, {
            PlayerMap.runTasks()
            if (Duration.between(lastAutoSave, LocalDateTime.now()).seconds >= Property.AUTO_SAVE_TIME.toLong()) {
                lastAutoSave = LocalDateTime.now()
                logger.info("$prefix Automatically saving data")
                PlayerMap.saveData()
                BanData.saveData()
                SpawnData.saveData()
            }
        }, 0, 20)

        logger.info("$prefix ${plugin.description.name} ${plugin.description.version} has been enabled.")
    }

    override fun onDisable() {
        PlayerMap.saveData()
        BanData.saveData()
        SpawnData.saveData()

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