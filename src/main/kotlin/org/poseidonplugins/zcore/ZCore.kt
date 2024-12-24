package org.poseidonplugins.zcore

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.config.Configuration
import org.poseidonplugins.commandapi.CommandManager
import org.poseidonplugins.zcore.commands.*
import org.poseidonplugins.zcore.config.Property
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.data.SpawnData
import org.poseidonplugins.zcore.listeners.PlayerListener
import org.poseidonplugins.zcore.player.PlayerMap
import java.io.File
import java.nio.file.Files
import java.time.Duration
import java.time.LocalDateTime
import java.util.logging.Logger

class ZCore : JavaPlugin() {

    companion object {
        const val prefix = "[ZCore]"
        lateinit var plugin: Plugin; private set
        lateinit var dataFolder: File; private set
        lateinit var logger: Logger; private set
        lateinit var cmdManager: CommandManager; private set
        lateinit var config: Configuration; private set
    }

    private var lastAutoSave: LocalDateTime = LocalDateTime.now()

    override fun onEnable() {
        plugin = this
        Companion.dataFolder = plugin.dataFolder
        logger = server.logger

        if (!dataFolder.exists()) dataFolder.mkdirs()
        initConfig()

        cmdManager = CommandManager(plugin)
        cmdManager.registerCommands(
            CommandBan(),
            CommandBanIP(),
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
            CommandKill(),
            CommandList(),
            CommandMotd(),
            CommandReload(),
            CommandRules(),
            CommandSeen(),
            CommandSetHome(),
            CommandSetSpawn(),
            CommandSpawn(),
            CommandTP(),
            CommandUnban(),
            CommandUnbanIP(),
            CommandVanish(),
            CommandZCore()
        )

        server.pluginManager.registerEvents(PlayerListener(), plugin)

        server.scheduler.scheduleAsyncRepeatingTask(plugin, {
            PlayerMap.runTasks()
            if (Duration.between(lastAutoSave, LocalDateTime.now()).seconds >= Property.AUTO_SAVE_TIME.toULong()) {
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

    fun initConfig() {
        val file = File(dataFolder, "config.yml")
        if (!file.exists()) {
            try {
                val stream = this::class.java.getResourceAsStream("/config.yml")!!
                file.parentFile.mkdirs()
                Files.copy(stream, file.toPath())
            } catch (e: Exception) {
                logger.severe("$prefix Failed to create config.")
                logger.severe("$prefix If the issue persists, unzip the plugin JAR and copy config.yml to ${dataFolder.path}")
                throw e
            }
        }
        config = Configuration(file)
        config.load()
    }
}