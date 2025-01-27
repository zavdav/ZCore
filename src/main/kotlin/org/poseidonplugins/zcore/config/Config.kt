package org.poseidonplugins.zcore.config

import org.bukkit.util.config.Configuration
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.commands.ZCoreCommand
import org.poseidonplugins.zcore.util.Utils.roundTo
import java.io.File
import java.nio.file.Files

object Config {

    private val file: File = File(ZCore.dataFolder, "config.yml")
    private lateinit var yaml: Configuration

    fun load() {
        if (!file.exists()) {
            try {
                val stream = this::class.java.getResourceAsStream("/config.yml")!!
                file.parentFile.mkdirs()
                Files.copy(stream, file.toPath())
            } catch (e: Exception) {
                ZCore.logger.severe("${ZCore.prefix} Failed to create config.")
                ZCore.logger.severe("${ZCore.prefix} If the issue persists, unzip the plugin JAR and copy config.yml to ${ZCore.dataFolder.path}")
                throw e
            }
        }
        yaml = Configuration(file)
        yaml.load()
    }

    private fun getString(key: String, def: String): String =
        yaml.getProperty(key) as? String ?: def

    private fun getInt(key: String, range: IntRange = 0..Int.MAX_VALUE, def: Int): Int =
        (yaml.getProperty(key) as? Number)?.toInt()?.coerceIn(range) ?: def

    private fun getLong(key: String, range: LongRange = 0..Long.MAX_VALUE, def: Long): Long =
        (yaml.getProperty(key) as? Number)?.toLong()?.coerceIn(range) ?: def

    private fun getDouble(key: String, range: ClosedFloatingPointRange<Double> = 0.0..Double.MAX_VALUE, def: Double): Double =
        (yaml.getProperty(key) as? Number)?.toDouble()?.coerceIn(range) ?: def

    private fun getBoolean(key: String, def: Boolean): Boolean =
        yaml.getProperty(key) as? Boolean ?: def

    @Suppress("UNCHECKED_CAST")
    private fun getStringList(key: String, def: List<String>): List<String> =
        yaml.getProperty(key) as? List<String> ?: def

    val autoSaveTime: Long
        get() = getLong("autoSaveTime", 1..Long.MAX_VALUE, 300)

    val precacheAllPlayers: Boolean
        get() = getBoolean("precacheAllPlayers", false)

    val backupFolder: String
        get() = getString("backupFolder", "./backup")

    val motd: List<String>
        get() = getStringList("motd", emptyList())

    val rules: List<String>
        get() = getStringList("motd", emptyList())

    val afkTime: Int
        get() = getInt("afkTime", 1..Int.MAX_VALUE, 300)

    val afkKickTime: Int
        get() = getInt("afkKickTime", 1..Int.MAX_VALUE, 1800)

    val protectAfkPlayers: Boolean
        get() = getBoolean("protectAfkPlayers", false)

    val afkDelay: Int
        get() = getInt("afkDelay", def = 3)

    val teleportDelay: Int
        get() = getInt("teleportDelay", def = 3)

    val chatFormat: String
        get() = getString("chatFormat", "{DISPLAYNAME}§f: {MESSAGE}")

    val broadcastFormat: String
        get() = getString("broadcastFormat", "§d[Broadcast] {MESSAGE}")

    val joinMsgFormat: String
        get() = getString("joinMsgFormat", "§e{NAME} has joined the game.")

    val leaveMsgFormat: String
        get() = getString("leaveMsgFormat", "§e{NAME} has left the game.")

    val kickMsgFormat: String
        get() = getString("kickMsgFormat", "§e{NAME} has been kicked from the server.")

    val banMsgFormat: String
        get() = getString("banMsgFormat", "§e{NAME} has been banned from the server.")

    val nickPrefix: String
        get() = getString("nickPrefix", "~")

    val nickFormat: String
        get() = getString("nickFormat", "{PREFIX} §f{NICKNAME}§f {SUFFIX}")

    val chatRadius: Int
        get() = getInt("chatRadius", def = 0)

    val msgSendFormat: String
        get() = getString("msgSendFormat", "§7[me -> {NAME}§7] §f{MESSAGE}")

    val msgReceiveFormat: String
        get() = getString("msgReceiveFormat", "§7[{NAME}§7 -> me] §f{MESSAGE}")

    val disabledCommands: List<String>
        get() = getStringList("disabledCommands", emptyList())

    fun getCommandCost(command: ZCoreCommand): Double =
        getDouble("commandCosts.${command.name}", 0.0..maxBalance, 0.0).roundTo(2)

    val currency: String
        get() = getString("currency", "$")

    val maxBalance: Double
        get() = getDouble("maxBalance", 0.0..Economy.MAX_BALANCE, Economy.MAX_BALANCE).roundTo(2)

    val balancesPerPage: Int
        get() = getInt("balancesPerPage", 1..Int.MAX_VALUE, 10)

    val multipleHomes: Int
        get() = getInt("multipleHomes", 2..Int.MAX_VALUE, 10)

    val homesPerPage: Int
        get() = getInt("homesPerPage", 1..Int.MAX_VALUE, 50)
}