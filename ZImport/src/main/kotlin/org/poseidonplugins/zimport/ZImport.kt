package org.poseidonplugins.zimport

import com.earth2me.essentials.Essentials
import com.johnymuffin.beta.fundamentals.Fundamentals
import com.legacyminecraft.poseidon.PoseidonConfig
import org.bukkit.plugin.java.JavaPlugin
import org.poseidonplugins.zcore.util.syncDelayedTask
import org.poseidonplugins.zimport.config.Config
import org.poseidonplugins.zimport.hooks.plugins.CorePluginHook
import org.poseidonplugins.zimport.hooks.plugins.EssentialsHook
import org.poseidonplugins.zimport.hooks.plugins.FundamentalsHook
import org.poseidonplugins.zimport.task.TransferTask
import org.poseidonplugins.zimport.util.Logger
import java.io.File

class ZImport : JavaPlugin() {

    companion object {
        lateinit var plugin: ZImport; private set
        lateinit var dataFolder: File; private set

        fun disablePlugin() {
            plugin.isEnabled = false
        }
    }

    override fun onEnable() {
        plugin = this
        Companion.dataFolder = dataFolder
        Config.load()
        if (PoseidonConfig.getInstance().getConfigBoolean("settings.watchdog.enable") ||
            PoseidonConfig.getInstance().getConfigBoolean("settings.enable-watchdog"))
        {
            Logger.warning("Please disable Watchdog to use ZImport.")
            disablePlugin()
            return
        }

        val pluginHook: CorePluginHook
        try {
            when (Config.plugin.lowercase()) {
                "essentials" -> {
                    val essentials = server.pluginManager.getPlugin("Essentials") as Essentials
                    pluginHook = EssentialsHook(essentials)
                }
                "fundamentals" -> {
                    val fundamentals = server.pluginManager.getPlugin("Fundamentals") as Fundamentals
                    pluginHook = FundamentalsHook(fundamentals)
                }
                else -> {
                    Logger.severe("Unsupported plugin ${Config.plugin}")
                    disablePlugin()
                    return
                }
            }
        } catch (e: Exception) {
            Logger.severe("Plugin could not be hooked.")
            disablePlugin()
            return
        }

        if (isEnabled) {
            Logger.info("Transfer will start in 10 seconds...")
            syncDelayedTask(TransferTask(pluginHook), 200)
        }
    }

    override fun onDisable() {
       Logger.info("${description.name} ${description.version} has been disabled.")
    }
}