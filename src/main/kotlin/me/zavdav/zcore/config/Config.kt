package me.zavdav.zcore.config

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.api.Economy.roundTo2
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.Logger
import org.bukkit.util.config.Configuration
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object Config {

    private val file: File = File(ZCore.dataFolder, "config.yml")
    private lateinit var yaml: Configuration

    fun load() {
        val stream = this::class.java.getResourceAsStream("/config.yml")!!

        if (!file.exists()) {
            try {
                file.parentFile.mkdirs()
                Files.copy(stream, file.toPath())
                yaml = Configuration(file)
                yaml.load()
            } catch (e: Exception) {
                Logger.severe("Failed to create config.")
                Logger.severe("If the issue persists, unzip the plugin JAR and copy config.yml to ${ZCore.dataFolder.path}")
                throw e
            }
        } else {
            yaml = Configuration(file)
            yaml.load()

            val newFile = File(ZCore.dataFolder, "config-new.yml")
            try {
                Files.copy(stream, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                val newConfig = Configuration(newFile)
                newConfig.load()

                if ((newConfig.getString("configVersion").toIntOrNull() ?: 0) > configVersion) {
                    Logger.info("A new config version has been detected. The new config file has been created at config-new.yml")
                } else {
                    newFile.delete()
                }
            } catch (_: Exception) {
                Logger.warning("Failed to check for new config version.")
            }
        }
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

    @Suppress("UNCHECKED_CAST")
    private fun getMap(key: String, def: Map<String, Any>): Map<String, Any> =
        yaml.getProperty(key) as? Map<String, Any> ?: def

    val configVersion: Int
        get() = getInt("configVersion", def = 0)

    val prefix: String
        get() = getString("prefix", "&5» ")

    val errorPrefix: String
        get() = getString("errorPrefix", "&4[!] ")

    val autoSaveTime: Long
        get() = getLong("autoSaveTime", 1..Long.MAX_VALUE, 300)

    val precacheAllPlayers: Boolean
        get() = getBoolean("precacheAllPlayers", false)

    val backupFolder: String
        get() = getString("backupFolder", "./backup")

    val motd: List<String>
        get() = getStringList("motd", emptyList())

    val rules: List<String>
        get() = getStringList("rules", emptyList())

    val teleportDelay: Int
        get() = getInt("teleportDelay", def = 3)

    val giveAmount: Int
        get() = getInt("giveAmount", 1..Int.MAX_VALUE, 64)

    val listOtherCommands: Boolean
        get() = getBoolean("listOtherCommands", true)

    val commandsPerPage: Int
        get() = getInt("commandsPerPage", 1..Int.MAX_VALUE, 10)

    val chat: String
        get() = getString("chat", "{DISPLAYNAME}§f: {MESSAGE}")

    val broadcast: String
        get() = getString("broadcast", "§d[Broadcast] {MESSAGE}")

    val sendMsg: String
        get() = getString("sendMsg", "§7[me -> {NAME}§7] §f{MESSAGE}")

    val receiveMsg: String
        get() = getString("receiveMsg", "§7[{NAME}§7 -> me] §f{MESSAGE}")

    val mail: String
        get() = getString("mail", "{NAME}&f: {MESSAGE}")

    val socialSpy: String
        get() = getString("socialSpy", "§6[SocialSpy] §f{DISPLAYNAME}§f: {COMMAND}")

    val joinMsg: String
        get() = getString("joinMsg", "§e{NAME} has joined the game.")

    val leaveMsg: String
        get() = getString("leaveMsg", "§e{NAME} has left the game.")

    val kickMsg: String
        get() = getString("kickMsg", "§e{NAME} has been kicked from the server.")

    val banMsg: String
        get() = getString("banMsg", "§e{NAME} has been banned from the server.")

    val operatorColor: String
        get() {
            val color = getString("operatorColor", "none")
            return if (color.matches("[0-9a-f]".toRegex())) "§$color" else ""
        }

    val nickPrefix: String
        get() = getString("nickPrefix", "~")

    val displayNameFormat: String
        get() = getString("displayNameFormat", "{PREFIX} §f{NICKNAME}§f {SUFFIX}")

    val chatRadius: Int
        get() = getInt("chatRadius", def = 0)

    val firstJoinMessage: String
        get() = getString("firstJoinMessage", "§dWelcome to the server, {DISPLAYNAME}§d!")

    val disabledCommands: List<String>
        get() = getStringList("disabledCommands", emptyList())

    fun getCommandCost(command: AbstractCommand): Double =
        getDouble("commandCosts.${command.name}", 0.0..maxBalance, 0.0).roundTo2()

    val currency: String
        get() = getString("currency", "$")

    val maxBalance: Double
        get() = getDouble("maxBalance", 0.0..Economy.MAX_BALANCE, Economy.MAX_BALANCE).roundTo2()

    val balancesPerPage: Int
        get() = getInt("balancesPerPage", 1..Int.MAX_VALUE, 10)

    val multipleHomes: Int
        get() = getInt("multipleHomes", 2..Int.MAX_VALUE, 10)

    val homesPerPage: Int
        get() = getInt("homesPerPage", 1..Int.MAX_VALUE, 50)

    val afkTime: Int
        get() = getInt("afkTime", 1..Int.MAX_VALUE, 300)

    val afkKickTime: Int
        get() = getInt("afkKickTime", 1..Int.MAX_VALUE, 1800)

    val protectAfkPlayers: Boolean
        get() = getBoolean("protectAfkPlayers", false)

    val afkDelay: Int
        get() = getInt("afkDelay", def = 3)

    val kits: Map<String, Any>
        get() = getMap("kits", emptyMap()).mapKeys { it.key.lowercase() }
}