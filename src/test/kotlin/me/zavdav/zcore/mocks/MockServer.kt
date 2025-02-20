package me.zavdav.zcore.mocks

import com.avaje.ebean.config.ServerConfig
import org.bukkit.OfflinePlayer
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.entity.Player
import org.bukkit.generator.ChunkGenerator
import org.bukkit.inventory.Recipe
import org.bukkit.map.MapView
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.ServicesManager
import org.bukkit.scheduler.BukkitScheduler
import java.util.*
import java.util.logging.Logger

object MockServer : Server {

    private val worlds: List<MockWorld> = listOf(
        MockWorld("world", World.Environment.NORMAL),
        MockWorld("world_nether", World.Environment.NETHER)
    )
    val players: MutableList<MockPlayer> = mutableListOf()
    val chat: MutableList<String> = mutableListOf()

    override fun getGameVersion(): String {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getName(): String = "ZCore Test Server"

    override fun getServerEnvironment(): String {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPoseidonVersion(): String {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPoseidonReleaseType(): String {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getVersion(): String = "1.0"

    override fun getOnlinePlayers(): Array<Player> = players.toTypedArray()

    override fun getMaxPlayers(): Int = 100

    override fun getPort(): Int = 25565

    override fun getViewDistance(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getIp(): String = "127.0.0.1"

    override fun getServerName(): String {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getServerId(): String {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getAllowNether(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun hasWhitelist(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setWhitelist(p0: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getWhitelistedPlayers(): MutableSet<OfflinePlayer> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun reloadWhitelist() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun broadcastMessage(message: String?): Int {
        chat.add(message ?: "")
        return 0
    }

    override fun getUpdateFolder(): String {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPlayer(name: String?): Player? {
        name ?: throw IllegalArgumentException("UUID cannot be null")
        return players.firstOrNull { it.name.equals(name, true) }
    }

    override fun getPlayer(uuid: UUID?): Player? {
        uuid ?: throw IllegalArgumentException("UUID cannot be null")
        return players.firstOrNull { it.uniqueId == uuid }
    }

    override fun getPlayerExact(p0: String?): Player {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun matchPlayer(string: String?): List<Player> {
        string ?: throw IllegalArgumentException("String cannot be null")
        val matches = mutableListOf<Player>()
        for (player in players) {
            if (string.equals(player.name, true)) {
                matches.clear()
                matches.add(player)
                break
            }

            if (player.name.lowercase().indexOf(string.lowercase()) != -1) {
                matches.add(player)
            }
        }
        return matches.toList()
    }

    override fun getPluginManager(): PluginManager {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getScheduler(): BukkitScheduler {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getServicesManager(): ServicesManager {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getWorlds(): List<World> = worlds

    override fun createWorld(p0: String?, p1: World.Environment?): World {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun createWorld(p0: String?, p1: World.Environment?, p2: Long): World {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun createWorld(p0: String?, p1: World.Environment?, p2: ChunkGenerator?): World {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun createWorld(p0: String?, p1: World.Environment?, p2: Long, p3: ChunkGenerator?): World {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun unloadWorld(p0: String?, p1: Boolean): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun unloadWorld(p0: World?, p1: Boolean): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getWorld(name: String?): World? {
        name ?: throw IllegalArgumentException("Name cannot be null")
        return worlds.firstOrNull { it.name == name }
    }

    override fun getWorld(p0: UUID?): World {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getMap(p0: Short): MapView {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun createMap(p0: World?): MapView {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun reload() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getLogger(): Logger = Logger.getLogger("Minecraft")

    override fun getPluginCommand(p0: String?): PluginCommand {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun savePlayers() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun dispatchCommand(p0: CommandSender?, p1: String?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun configureDbConfig(p0: ServerConfig?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun addRecipe(p0: Recipe?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getCommandAliases(): MutableMap<String, Array<String>> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getSpawnRadius(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setSpawnRadius(p0: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getOnlineMode(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getAllowFlight(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun shutdown() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun broadcast(p0: String?, p1: String?): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getOfflinePlayer(p0: String?): OfflinePlayer {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getIPBans(): MutableSet<String> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun banIP(p0: String?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun unbanIP(p0: String?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getBannedPlayers(): MutableSet<OfflinePlayer> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isCommandHidden(p0: String?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun addHiddenCommand(p0: String?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun addHiddenCommands(p0: MutableList<String>?) {
        throw UnsupportedOperationException("Operation not supported.")
    }
}