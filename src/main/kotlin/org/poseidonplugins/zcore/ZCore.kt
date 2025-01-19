package org.poseidonplugins.zcore

import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.poseidonplugins.commandapi.CommandManager
import org.poseidonplugins.zcore.commands.*
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.data.SpawnData
import org.poseidonplugins.zcore.data.WarpData
import org.poseidonplugins.zcore.listeners.EntityListener
import org.poseidonplugins.zcore.listeners.PlayerListener
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.Backup
import org.poseidonplugins.zcore.util.asyncRepeatingTask
import java.io.File
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
    }

    private var lastAutoSave: LocalDateTime = LocalDateTime.now()

    override fun onEnable() {
        plugin = this
        Companion.dataFolder = plugin.dataFolder
        logger = server.logger

        if (!dataFolder.exists()) dataFolder.mkdirs()
        Config.load()
        Backup.init()

        val commands = listOf(
            CommandAFK(),
            CommandBackup(),
            CommandBalance(),
            CommandBalanceTop(),
            CommandBan(),
            CommandBanIP(),
            CommandBroadcast(),
            CommandClearInv(),
            CommandDelHome(),
            CommandDelWarp(),
            CommandEconomy(),
            CommandGod(),
            CommandHeal(),
            CommandHelp(),
            CommandHome(),
            CommandHomes(),
            CommandIgnore(),
            CommandIgnoreList(),
            CommandInvSee(),
            CommandKick(),
            CommandKickAll(),
            CommandKill(),
            CommandList(),
            CommandMail(),
            CommandMotd(),
            CommandMsg(),
            CommandNick(),
            CommandPay(),
            CommandRealName(),
            CommandReload(),
            CommandReply(),
            CommandRules(),
            CommandSeed(),
            CommandSeen(),
            CommandSetHome(),
            CommandSetSpawn(),
            CommandSetWarp(),
            CommandSpawn(),
            CommandTP(),
            CommandUnban(),
            CommandUnbanIP(),
            CommandVanish(),
            CommandWarp(),
            CommandWeather(),
            CommandZCore()
        ).filter { it.name !in Config.getList("disabledCommands") }

        cmdManager = CommandManager(plugin)
        cmdManager.registerCommands(*commands.toTypedArray())

        server.pluginManager.registerEvents(EntityListener(), plugin)
        server.pluginManager.registerEvents(PlayerListener(), plugin)

        asyncRepeatingTask({
            PlayerMap.runTasks()
            if (Duration.between(lastAutoSave, LocalDateTime.now()).seconds >= Config.getLong("autoSaveTime", 1)) {
                lastAutoSave = LocalDateTime.now()
                logger.info("$prefix Automatically saving data")
                PlayerMap.saveData()
                BanData.saveData()
                SpawnData.saveData()
                WarpData.saveData()
            }
        }, 0, 20)

        logger.info("$prefix ${plugin.description.name} ${plugin.description.version} has been enabled.")
    }

    override fun onDisable() {
        PlayerMap.saveData()
        BanData.saveData()
        SpawnData.saveData()
        WarpData.saveData()

        logger.info("$prefix ${plugin.description.name} ${plugin.description.version} has been disabled.")
    }
}