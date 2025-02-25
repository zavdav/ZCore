package me.zavdav.zcore

import me.zavdav.zcore.commands.*
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.config.Items
import me.zavdav.zcore.config.Kits
import me.zavdav.zcore.data.BannedIPs
import me.zavdav.zcore.data.Spawnpoints
import me.zavdav.zcore.data.UUIDCache
import me.zavdav.zcore.data.Warps
import me.zavdav.zcore.listeners.EntityListener
import me.zavdav.zcore.listeners.PlayerListener
import me.zavdav.zcore.user.UserMap
import me.zavdav.zcore.util.Backup
import me.zavdav.zcore.util.Logger
import me.zavdav.zcore.util.syncRepeatingTask
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.java.JavaPlugin
import org.poseidonplugins.commandapi.CommandManager
import java.io.File
import java.io.FileReader
import java.net.URLClassLoader

class ZCore : JavaPlugin() {

    companion object {
        lateinit var plugin: Plugin; private set
        lateinit var dataFolder: File; private set
        lateinit var cmdManager: CommandManager; private set
    }

    private var lastAutoSave: Long = System.currentTimeMillis()

    override fun onEnable() {
        plugin = this
        Companion.dataFolder = dataFolder
        if (!dataFolder.exists()) dataFolder.mkdirs()

        Config.load()
        Items.load()
        Kits.load()
        Backup.load()
        UserMap
        UUIDCache
        BannedIPs
        Spawnpoints
        Warps

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
            CommandGive(),
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
            CommandKit(),
            CommandList(),
            CommandMail(),
            CommandMotd(),
            CommandMsg(),
            CommandMute(),
            CommandNick(),
            CommandPay(),
            CommandPlayTime(),
            CommandPTime(),
            CommandPunishments(),
            CommandR(),
            CommandRealName(),
            CommandReload(),
            CommandRules(),
            CommandSeed(),
            CommandSeen(),
            CommandSetHome(),
            CommandSetSpawn(),
            CommandSetWarp(),
            CommandSmite(),
            CommandSocialSpy(),
            CommandSpawn(),
            CommandSummon(),
            CommandTime(),
            CommandToggleChat(),
            CommandTP(),
            CommandTpa(),
            CommandTpAccept(),
            CommandTpaHere(),
            CommandTpDeny(),
            CommandUnban(),
            CommandUnbanIP(),
            CommandUnmute(),
            CommandVanish(),
            CommandWarp(),
            CommandWeather(),
            CommandZCore()
        ).filter { it.name !in Config.disabledCommands }

        cmdManager = CommandManager(this)
        cmdManager.registerCommands(*commands.toTypedArray())

        server.pluginManager.registerEvents(EntityListener(), this)
        server.pluginManager.registerEvents(PlayerListener(), this)

        syncRepeatingTask(0, 20) {
            runCatching {
                UserMap.checkOnlineUsers()
                if (System.currentTimeMillis() - lastAutoSave >= Config.autoSaveTime * 1000) {
                    lastAutoSave = System.currentTimeMillis()
                    Logger.info("Automatically saving data")
                    saveData(true, false)
                }
            }.onFailure { it.printStackTrace() }
        }

        Logger.info("${description.name} ${description.version} has been enabled.")
    }

    override fun onDisable() {
        saveData(false, true)
        Logger.info("${description.name} ${description.version} has been disabled.")
    }

    fun saveData(async: Boolean, force: Boolean) {
        UserMap.saveData(async, force)
        UUIDCache.saveData(async, force)
        BannedIPs.saveData(async, force)
        Spawnpoints.saveData(async, force)
        Warps.saveData(async, force)
    }

    fun setupForTesting(server: Server) {
        val dataFolder = File("build/tmp/test/ZCoreTest")
        dataFolder.mkdirs()
        dataFolder.deleteOnExit()

        val description = PluginDescriptionFile(FileReader("src/main/resources/plugin.yml"))
        val classLoader = URLClassLoader(arrayOf(File("src/main/resources").toURI().toURL()))
        Bukkit.setServer(server)
        initialize(null, server, description, dataFolder, null, classLoader)

        plugin = this
        Companion.dataFolder = dataFolder
        if (!dataFolder.exists()) dataFolder.mkdirs()

        Config.load()
        Backup.load()
    }
}