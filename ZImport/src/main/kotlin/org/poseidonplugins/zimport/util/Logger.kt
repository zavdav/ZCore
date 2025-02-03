package org.poseidonplugins.zimport.util

import org.bukkit.Bukkit
import java.util.logging.Logger

object Logger {

    private val logger: Logger = Bukkit.getLogger()

    fun info(message: String) {
        logger.info("[ZImport] $message")
    }

    fun warning(message: String) {
        logger.warning("[ZImport] $message")
    }

    fun severe(message: String) {
        logger.severe("[ZImport] $message")
    }
}