package org.betamc.core

import org.bukkit.plugin.java.JavaPlugin

class BMCPlugin : JavaPlugin() {

    override fun onEnable() = BMCCore.enable(this)

    override fun onDisable() = BMCCore.disable()

}