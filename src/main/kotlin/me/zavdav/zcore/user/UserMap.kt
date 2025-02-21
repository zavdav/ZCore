package me.zavdav.zcore.user

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.Logger
import me.zavdav.zcore.util.Utils
import java.io.File
import java.util.*
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
                Logger.warning("Found corrupt UUID: $uuid")
                continue
            }
            knownUsers.add(UUID.fromString(uuid))
        }

        precacheAll = Config.precacheAllPlayers
        if (precacheAll) {
            for (uuid in knownUsers) {
                User.from(uuid)
            }
            Logger.info("Precached ${userMap.size} user(s).")
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
            if (!user.isOnline && System.currentTimeMillis() - user.lastSeen >= 600 * 1000) {
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