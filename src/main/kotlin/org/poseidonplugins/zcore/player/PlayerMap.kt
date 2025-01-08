package org.poseidonplugins.zcore.player

import org.bukkit.entity.Player
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.Utils
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object PlayerMap {

    private val playerMap: MutableMap<UUID, ZPlayer> = ConcurrentHashMap<UUID, ZPlayer>()
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
        if (!isPlayerKnown(uuid)) knownPlayers.add(uuid)
        if (uuid in playerMap.keys) return playerMap[uuid]!!

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

    fun runTasks() {
        playerMap.entries.removeIf { entry ->
            val zPlayer = getPlayer(entry.key)
            if (!zPlayer.isOnline && Duration.between(zPlayer.lastSeen, LocalDateTime.now()).seconds >= 600) {
                zPlayer.saveData()
                !precacheAll
            } else {
                zPlayer.checkIsAfk()
                false
            }
        }
    }

    fun isPlayerKnown(uuid: UUID): Boolean = uuid in knownPlayers

    fun saveData() {
        for (zPlayer in playerMap.values) {
            zPlayer.saveData()
        }
    }
}