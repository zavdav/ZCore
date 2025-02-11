package org.poseidonplugins.zimport.config

import org.bukkit.util.config.Configuration
import org.poseidonplugins.zimport.ZImport
import org.poseidonplugins.zimport.util.Logger
import java.io.File
import java.nio.file.Files

object Config {

    private val file: File = File(ZImport.dataFolder, "config.yml")
    private lateinit var yaml: Configuration

    fun load() {
        if (!file.exists()) {
            try {
                val stream = this::class.java.getResourceAsStream("/config.yml")!!
                file.parentFile.mkdirs()
                Files.copy(stream, file.toPath())
                Logger.info("Plugin has started for the first time. Please confirm your config is set correctly.")
                ZImport.disablePlugin()
            } catch (e: Exception) {
                Logger.severe("Failed to create config.")
                Logger.severe("If the issue persists, unzip the plugin JAR and copy config.yml to ${ZImport.dataFolder.path}")
                throw e
            }
        }
        yaml = Configuration(file)
        yaml.load()
    }

    private fun getString(key: String): String =
        yaml.getProperty(key) as? String ?: "null"

    private fun getBoolean(key: String): Boolean =
        yaml.getProperty(key) as? Boolean == true

    val plugin: String
        get() = getString("plugin")

    val replaceExisting: Boolean
        get() = getBoolean("replaceExisting")

    val transferBalances: Boolean
        get() = getBoolean("transferBalances")

    val transferHomes: Boolean
        get() = getBoolean("transferHomes")

    val transferPunishments: Boolean
        get() = getBoolean("transferPunishments")
}