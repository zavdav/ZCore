package me.zavdav.zcore.mocks

import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.permissions.Permissible
import org.bukkit.permissions.Permission
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.PluginLoader
import org.bukkit.plugin.PluginManager
import java.io.File

class MockPluginManager : PluginManager {

    override fun registerInterface(p0: Class<out PluginLoader>?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPlugin(p0: String?): Plugin {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPlugins(): Array<Plugin> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isPluginEnabled(p0: String?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isPluginEnabled(p0: Plugin?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun loadPlugin(p0: File?): Plugin {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun loadPlugins(p0: File?): Array<Plugin> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun disablePlugins() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun clearPlugins() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun callEvent(p0: Event?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun registerEvent(p0: Event.Type?, p1: Listener?, p2: Event.Priority?, p3: Plugin?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun registerEvent(p0: Event.Type?, p1: Listener?, p2: EventExecutor?, p3: Event.Priority?, p4: Plugin?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun registerEvents(p0: Listener?, p1: Plugin?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun enablePlugin(p0: Plugin?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun disablePlugin(p0: Plugin?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPermission(p0: String?): Permission {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun addPermission(p0: Permission?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun removePermission(p0: Permission?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun removePermission(p0: String?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getDefaultPermissions(p0: Boolean): MutableSet<Permission> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun recalculatePermissionDefaults(p0: Permission?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun subscribeToPermission(p0: String?, p1: Permissible?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun unsubscribeFromPermission(p0: String?, p1: Permissible?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPermissionSubscriptions(p0: String?): MutableSet<Permissible> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun subscribeToDefaultPerms(p0: Boolean, p1: Permissible?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun unsubscribeFromDefaultPerms(p0: Boolean, p1: Permissible?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getDefaultPermSubscriptions(p0: Boolean): MutableSet<Permissible> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPermissions(): MutableSet<Permission> {
        throw UnsupportedOperationException("Operation not supported.")
    }
}