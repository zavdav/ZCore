package me.zavdav.zcore

import me.zavdav.zcore.commands.*
import me.zavdav.zcore.commands.core.CommandManager
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
import me.zavdav.zcore.util.getField
import me.zavdav.zcore.util.syncDelayedTask
import me.zavdav.zcore.util.syncRepeatingTask
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.event.Event
import org.bukkit.plugin.PluginDescriptionFile
import org.bukkit.plugin.RegisteredListener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileReader
import java.net.URLClassLoader
import java.util.SortedSet

class ZCore : JavaPlugin() {

    companion object {
        lateinit var INSTANCE: ZCore; private set
        lateinit var dataFolder: File; private set
    }

    private var lastAutoSave: Long = System.currentTimeMillis()

    override fun onEnable() {
        INSTANCE = this
        Companion.dataFolder = dataFolder
        if (!dataFolder.exists()) dataFolder.mkdirs()

        Logger.info("Loading configuration")
        Config.load()
        Items.load()
        Kits.load()
        Backup.load()

        Logger.info("Loading data into memory")
        UserMap
        UUIDCache
        BannedIPs
        Spawnpoints
        Warps

        Logger.info("Registering commands")
        val commands = mutableListOf(
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
            CommandItem(),
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
        )

        commands.removeAll { it.name in Config.disabledCommands }
        val overrides = commands.filter { it.name in Config.overrideCommands }
        commands.removeAll(overrides)

        CommandManager.registerCommands(*commands.toTypedArray())
        syncDelayedTask {
            for (command in overrides) {
                val matches = CommandManager.knownCommands.filter { it.value.name.equals(command.name, true) }
                matches.forEach {
                    CommandManager.knownCommands.remove(it.key)
                    it.value.unregister(CommandManager.commandMap)
                }
                CommandManager.registerCommands(command)
                matches.forEach { CommandManager.commandMap.register(it.key, it.value) }
            }
        }

        Logger.info("Registering listeners")
        server.pluginManager.registerEvents(EntityListener(), this)
        server.pluginManager.registerEvents(PlayerListener(), this)

        Logger.info("Starting tasks")
        syncRepeatingTask(0, 20) {
            runCatching {
                UserMap.checkOnlineUsers()
                if (System.currentTimeMillis() - lastAutoSave >= Config.autoSaveTime * 1000) {
                    lastAutoSave = System.currentTimeMillis()
                    saveData(true, false)
                }
            }.onFailure { it.printStackTrace() }
        }

        Logger.info("Version ${description.version} has been enabled.")
    }

    @Suppress("UNCHECKED_CAST")
    override fun onDisable() {
        saveData(false, true)
        Logger.info("Unregistering commands")
        CommandManager.unregisterAll()
        Logger.info("Unregistering listeners")
        val listeners = getField<MutableMap<Event.Type, SortedSet<RegisteredListener>>>(server.pluginManager, "listeners")
        listeners!!.entries.forEach { it.value.removeIf { it.plugin is ZCore } }
        Logger.info("Version ${description.version} has been disabled.")
    }

    fun reload() {
        isEnabled = false
        isEnabled = true
    }

    fun saveData(async: Boolean, force: Boolean) {
        Logger.info("Saving data to disk")
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

        INSTANCE = this
        Companion.dataFolder = dataFolder
        if (!dataFolder.exists()) dataFolder.mkdirs()

        Config.load()
        Backup.load()
    }
}