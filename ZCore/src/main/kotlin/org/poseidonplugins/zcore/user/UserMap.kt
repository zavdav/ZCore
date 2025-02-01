package org.poseidonplugins.zcore.user

import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.Utils
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object UserMap {

    val userMap: MutableMap<UUID, User> = ConcurrentHashMap<UUID, User>()
    val knownUsers: MutableSet<UUID> = mutableSetOf()
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
            knownUsers.add(UUID.fromString(uuid))
        }

        precacheAll = Config.precacheAllPlayers
        if (precacheAll) {
            for (uuid in knownUsers) {
                User.from(uuid)
            }
            ZCore.logger.info("${ZCore.prefix} Precached ${userMap.size} users(s).")
        }
    }

    fun getAllUsers(): Set<User> {
        if (precacheAll) return userMap.values.toSet()
        val players = mutableSetOf<User>()
        for (uuid in knownUsers) {
            players.add(User.from(uuid, false))
        }
        return players.toSet()
    }

    fun runTasks() {
        userMap.entries.removeIf { entry ->
            val user = User.from(entry.key)
            if (!user.isOnline && Duration.between(user.lastSeen, LocalDateTime.now()).seconds >= 600) {
                user.saveData()
                !precacheAll
            } else {
                user.checkIsAfk()
                false
            }
        }
    }

    fun isUserKnown(uuid: UUID): Boolean = uuid in knownUsers

    fun saveData() {
        for (user in userMap.values) {
            user.saveData()
        }
    }
}