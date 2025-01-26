package org.poseidonplugins.zcore.config

import org.bukkit.util.config.Configuration
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.commands.ZCoreCommand
import org.poseidonplugins.zcore.util.Utils.roundTo
import org.poseidonplugins.zcore.util.Utils.toBooleanOrDefault
import org.poseidonplugins.zcore.util.Utils.toDoubleOrDefault
import org.poseidonplugins.zcore.util.Utils.toIntOrDefault
import org.poseidonplugins.zcore.util.Utils.toLongOrDefault
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

    fun getString(key: String): String =
        (yaml.getProperty(key) ?: defaults[key]).toString()

    fun getInt(key: String, min: Int = 0, max: Int = Int.MAX_VALUE): Int =
        getString(key).toIntOrDefault(defaults[key].toString().toInt()).coerceIn(min, max)

    fun getLong(key: String, min: Long = 0, max: Long = Long.MAX_VALUE): Long =
        getString(key).toLongOrDefault(defaults[key].toString().toLong()).coerceIn(min, max)

    fun getDouble(key: String, min: Double = 0.0, max: Double = Double.MAX_VALUE): Double =
        getString(key).toDoubleOrDefault(defaults[key].toString().toDouble()).coerceIn(min, max)

    fun getBoolean(key: String): Boolean =
        getString(key).toBooleanOrDefault(defaults[key].toString().toBooleanStrict())

    fun getList(key: String): List<String> =
        try { yaml.getProperty(key) as List<String> }
        catch (_: Exception) { defaults[key] as List<String> }

    fun isEmpty(key: String): Boolean {
        return (yaml.getProperty(key) ?: return false).toString().isEmpty()
    }

    fun getCommandCost(command: ZCoreCommand) =
        (yaml.getProperty("commandCosts.${command.name}") as? Double ?: 0.0)
            .coerceIn(0.0, Economy.MAX_BALANCE)
            .roundTo(2)

    private val defaults: Map<String, Any> = mapOf(
        "autoSaveTime" to 300,
        "precacheAllPlayers" to false,
        "backupFolder" to "./backup",
        "motd" to mutableListOf("§eWelcome, {NAME}§e!", "§bType /help for a list of commands.", "§7Online players: §f{PLAYERLIST}"),
        "rules" to mutableListOf("§c1. Be respectful", "§c2. No griefing", "§c3. No cheating"),
        "afkTime" to 300,
        "afkKickTime" to 1800,
        "protectAfkPlayers" to false,
        "afkDelay" to 3,
        "teleportDelay" to 3,
        "chatFormat" to "{DISPLAYNAME}§f: {MESSAGE}",
        "broadcastFormat" to "§d[Broadcast] {MESSAGE}",
        "joinMsgFormat" to "§e{PLAYER} has joined the game.",
        "leaveMsgFormat" to "§e{PLAYER} has left the game.",
        "kickMsgFormat" to "§e{PLAYER} has been kicked from the server.",
        "banMsgFormat" to "§e{PLAYER} has been banned from the server.",
        "nickPrefix" to "~",
        "nickFormat" to "{PREFIX} §f{NICKNAME}§f {SUFFIX}",
        "chatRadius" to 0,
        "msgSendFormat" to "§7[me -> {NAME}§7] §f{MESSAGE}",
        "msgReceiveFormat" to "§7[{NAME}§7 -> me] §f{MESSAGE}",
        "disabledCommands" to listOf<String>(),
        "currency" to "$",
        "maxBalance" to 10000000000000,
        "balancesPerPage" to 10,
        "multipleHomes" to 10,
        "homesPerPage" to 50,
    )
}