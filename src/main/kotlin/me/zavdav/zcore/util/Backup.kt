package me.zavdav.zcore.util

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.data.BannedIPs
import me.zavdav.zcore.data.Spawnpoints
import me.zavdav.zcore.data.Warps
import me.zavdav.zcore.user.UserMap
import org.bukkit.command.CommandSender
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.Path

object Backup {

    private val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH.mm.ss.SSS")
    private lateinit var root: File

    fun load() {
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

        try {
            val folder = File(root, "ZCore-${dtf.format(LocalDateTime.now())}")
            folder.mkdirs()

            val userdata = File(folder, "userdata")
            val bannedIps = File(folder, "bannedips.json")
            val spawnpoints = File(folder, "spawnpoints.json")
            val warps = File(folder, "warps.json")
            BannedIPs.saveData(false, true, bannedIps)
            Spawnpoints.saveData(false, true, spawnpoints)
            Warps.saveData(false, true, warps)
            userdata.mkdirs()
            for (user in UserMap.getAllUsers()) {
                val data = File(userdata, "${user.uuid}.json")
                user.saveData(false, true, data)
            }

            val time = System.currentTimeMillis() - start
            sender.sendTl("backupSuccess", "file" to folder.name, "millis" to time)
        } catch (e: Exception) {
            e.printStackTrace()
            throw CommandException(tl("backupFailed"))
        }
    }
}