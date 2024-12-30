package org.poseidonplugins.zcore.player

import org.bukkit.entity.Player
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.Utils
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID

object PlayerMap {

    private val playerMap: MutableMap<UUID, ZPlayer> = mutableMapOf()
    private val knownPlayers: MutableSet<UUID> = mutableSetOf()
    private val precacheAll: Boolean

    init {
        val dataFolder = File(ZCore.dataFolder, "userdata")
        if (!dataFolder.exists()) dataFolder.mkdirs()

        for (file in dataFolder.list()!!) {
            if (!file.endsWith(".json")) continue
            val uuid = file.replace(".json", "")
            if (!Utils.UUID_PATTERN.matcher(uuid).matches()) {
                ZCore.logger.warning("${ZCore.prefix} Found corrupt UUID: $uuid")
                continue
            }
            knownPlayers.add(UUID.fromString(uuid))
        }

        precacheAll = Config.getBoolean("precacheAllPlayers")
        if (precacheAll) {
            for (uuid in knownPlayers) {
                getPlayer(uuid)
            }
            ZCore.logger.info("${ZCore.prefix} Precached ${playerMap.size} player(s).")
        }
    }

    fun getPlayer(uuid: UUID): ZPlayer {
        if (!knownPlayers.contains(uuid)) knownPlayers.add(uuid)
        if (playerMap.containsKey(uuid)) return playerMap[uuid]!!

        val zPlayer = ZPlayer(uuid)
        playerMap[uuid] = zPlayer
        return zPlayer
    }

    fun getPlayer(player: Player): ZPlayer = getPlayer(player.uniqueId)

    fun getAllPlayers(): Set<ZPlayer> {
        if (precacheAll) return playerMap.values.toSet()
        val players = mutableSetOf<ZPlayer>()
        for (uuid in knownPlayers) {
            players.add(playerMap[uuid] ?: ZPlayer(uuid))
        }
        return players.toSet()
    }

    @Synchronized
    fun runTasks() {
        playerMap.entries.removeIf { entry ->
            val zPlayer = getPlayer(entry.key)
            if (!precacheAll && !zPlayer.isOnline
                && Duration.between(zPlayer.lastSeen, LocalDateTime.now()).seconds >= 600) {
                zPlayer.saveData()
                true
            }
            else false
        }
    }

    fun isPlayerKnown(uuid: UUID): Boolean = knownPlayers.contains(uuid)

    @Synchronized
    fun saveData() {
        for (zPlayer in playerMap.values) {
            zPlayer.saveData()
        }
    }
}