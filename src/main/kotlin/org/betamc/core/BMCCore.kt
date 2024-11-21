package org.betamc.core

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

class BMCCore : JavaPlugin() {

    private lateinit var logger: Logger
    private val prefix = "[BetaMC Core]"

    override fun onEnable() {
        logger = Bukkit.getLogger()
        logger.info("$prefix Plugin has loaded, Version ${description.version}")
    }

    override fun onDisable() {
        logger.info("$prefix Stopping plugin")
    }

}