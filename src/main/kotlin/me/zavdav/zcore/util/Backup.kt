package me.zavdav.zcore.util

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.Punishments
import me.zavdav.zcore.data.SpawnData
import me.zavdav.zcore.data.WarpData
import me.zavdav.zcore.user.UserMap
import org.bukkit.command.CommandSender
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.locks.ReentrantLock
import kotlin.io.path.Path

object Backup {

    private val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss.SSS")
    private val lock: ReentrantLock = ReentrantLock(true)
    private lateinit var root: File

    fun init() {
        val path = Path(Config.backupFolder)
        root = if (path.isAbsolute) path.toFile()
               else Path(ZCore.dataFolder.path, path.toString()).normalize().toFile()
        if (root.exists() && !root.isDirectory) {
            Logger.severe("The file path to the backup folder points to a file, not a folder.")
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
                val folder = File(root, "ZCore-${dtf.format(LocalDateTime.now())}")
                folder.mkdirs()

                val punishments = File(folder, "punishments.json")
                punishments.createNewFile()
                Punishments.saveTo(punishments)

                val spawns = File(folder, "spawns.json")
                spawns.createNewFile()
                SpawnData.saveTo(spawns)

                val warps = File(folder, "warps.json")
                warps.createNewFile()
                WarpData.saveTo(warps)

                val userdata = File(folder, "userdata")
                userdata.mkdirs()
                for (user in UserMap.getAllUsers()) {
                    val data = File(userdata, "${user.uuid}.json")
                    data.createNewFile()
                    user.saveTo(data)
                }

                val time = System.currentTimeMillis() - start
                sender.sendTl("backupSuccess", "file" to folder.name, "millis" to time)
            } catch (e: Exception) {
                e.printStackTrace()
                throw AsyncCommandException(sender, tlError("backupFailed"))
            } finally {
                lock.unlock()
            }
        }
    }
}