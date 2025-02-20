package me.zavdav.zcore.util

import org.bukkit.Bukkit
import java.util.logging.Logger

object Logger {

    private val logger: Logger = Bukkit.getLogger()
    const val PREFIX: String = "[ZCore]"

    fun info(msg: String) {
        logger.info("$PREFIX $msg")
    }

    fun warning(msg: String) {
        logger.warning("$PREFIX $msg")
    }

    fun severe(msg: String) {
        logger.severe("$PREFIX $msg")
    }
}