package org.poseidonplugins.zcore.util

import org.bukkit.command.CommandSender
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.data.BanData
import org.poseidonplugins.zcore.data.SpawnData
import org.poseidonplugins.zcore.data.WarpData
import org.poseidonplugins.zcore.player.PlayerMap
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.locks.ReentrantLock
import kotlin.io.path.Path

object Backup {

    private val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    private val lock: ReentrantLock = ReentrantLock(true)
    private lateinit var root: File

    fun init() {
        val path = Path(Config.getString("backupFolder"))
        root = if (path.isAbsolute) path.toFile()
               else Path(ZCore.dataFolder.path, path.toString()).normalize().toFile()
        if (root.exists() && !root.isDirectory) {
            ZCore.logger.severe("${ZCore.prefix} The file path to the backup folder points to a file, not a folder.")
            throw IllegalArgumentException()
        }
    }

    fun run(sender: CommandSender) {
        val start = System.currentTimeMillis()
        if (!root.exists()) root.mkdirs()
        sender.sendTl("backupStarted")

        asyncDelayedTask {
            try {
                lock.lock()
                val folder = File(root, "/ZCore-${dtf.format(LocalDateTime.now())}")
                folder.mkdirs()

                val bans = File(folder, "bans.json")
                bans.createNewFile()
                BanData.saveTo(bans)

                val spawns = File(folder, "spawns.json")
                spawns.createNewFile()
                SpawnData.saveTo(spawns)

                val warps = File(folder, "warps.json")
                warps.createNewFile()
                WarpData.saveTo(warps)

                val userdata = File(folder, "userdata")
                userdata.mkdirs()
                for (zPlayer in PlayerMap.getAllPlayers()) {
                    val data = File(userdata, "${zPlayer.uuid}.json")
                    data.createNewFile()
                    zPlayer.saveTo(data)
                }

                val time = System.currentTimeMillis() - start
                sender.sendTl("backupSuccess", "file" to folder.name, "millis" to time)
            } catch (e: Exception) {
                e.printStackTrace()
                throw AsyncCommandException(sender, formatError("backupFailed"))
            } finally {
                lock.unlock()
            }
        }
    }
}