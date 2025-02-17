@file:JvmName("Scheduler")

package org.poseidonplugins.zcore.util

import org.bukkit.Bukkit
import org.poseidonplugins.zcore.ZCore

fun syncDelayedTask(runnable: Runnable): Int =
    Bukkit.getScheduler().scheduleSyncDelayedTask(ZCore.plugin, runnable)

fun syncDelayedTask(delay: Long, runnable: Runnable): Int =
    Bukkit.getScheduler().scheduleSyncDelayedTask(ZCore.plugin, runnable, delay)

fun syncRepeatingTask(delay: Long, interval: Long, runnable: Runnable): Int =
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

fun asyncDelayedTask(delay: Long, runnable: Runnable): Int =
    Bukkit.getScheduler().scheduleAsyncDelayedTask(ZCore.plugin, {
        try {
            runnable.run()
        } catch (e: AsyncCommandException) {
            for (message in e.messages) {
                e.sender.sendMessage(message)
            }
        }
    }, delay)

fun asyncRepeatingTask(delay: Long, interval: Long, runnable: Runnable): Int =
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