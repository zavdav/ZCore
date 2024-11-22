package org.betamc.core

import org.betamc.core.commands.*
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.poseidonplugins.commandapi.CommandManager
import java.util.logging.Logger

class BMCCore : JavaPlugin() {

    private lateinit var logger: Logger
    private val prefix = "[BMC-Core]"

    override fun onEnable() {
        logger = Bukkit.getLogger()
        CommandManager(this).registerCommands(CommandHelp(), CommandList())
        logger.info("$prefix Has loaded, Version ${description.version}")
    }

    override fun onDisable() {
        logger.info("$prefix Stopping plugin")
    }

}