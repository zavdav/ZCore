@file:JvmName("Scheduler")

package org.poseidonplugins.zcore.util

import org.bukkit.Bukkit
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.exceptions.AsyncCommandException

fun syncDelayedTask(runnable: Runnable): Int =
    Bukkit.getScheduler().scheduleSyncDelayedTask(ZCore.plugin, runnable)

fun syncDelayedTask(runnable: Runnable, delay: Long): Int =
    Bukkit.getScheduler().scheduleSyncDelayedTask(ZCore.plugin, runnable, delay)

fun syncRepeatingTask(runnable: Runnable, delay: Long, interval: Long): Int =
    Bukkit.getScheduler().scheduleSyncRepeatingTask(ZCore.plugin, runnable, delay, interval)

fun asyncDelayedTask(runnable: Runnable): Int =
    Bukkit.getScheduler().scheduleAsyncDelayedTask(ZCore.plugin) {
        try {
            runnable.run()
        } catch (e: AsyncCommandException) {
            for (message in e.messages) {
                e.sender.sendMessage(message)
            }
        }
    }

fun asyncDelayedTask(runnable: Runnable, delay: Long): Int =
    Bukkit.getScheduler().scheduleAsyncDelayedTask(ZCore.plugin, {
        try {
            runnable.run()
        } catch (e: AsyncCommandException) {
            for (message in e.messages) {
                e.sender.sendMessage(message)
            }
        }
    }, delay)

fun asyncRepeatingTask(runnable: Runnable, delay: Long, interval: Long): Int =
    Bukkit.getScheduler().scheduleAsyncRepeatingTask(ZCore.plugin, {
        try {
            runnable.run()
        } catch (e: AsyncCommandException) {
            for (message in e.messages) {
                e.sender.sendMessage(message)
            }
        }
    }, delay, interval)

fun cancelTask(taskId: Int) = Bukkit.getScheduler().cancelTask(taskId)