@file:JvmName("MessageUtils")

package me.zavdav.zcore.util

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.colorize
import java.util.ResourceBundle

private val bundle: ResourceBundle = ResourceBundle.getBundle("messages")

fun CommandSender.send(message: String, vararg pairs: Pair<String, Any>) =
    sendMessage(format(message, *pairs))

fun CommandSender.send(message: String, player: Player, vararg pairs: Pair<String, Any>) =
    sendMessage(format(message, player, *pairs))

fun CommandSender.sendTl(key: String, vararg pairs: Pair<String, Any>) =
    sendMessage(tl(key, *pairs))

fun CommandSender.sendTl(key: String, player: Player, vararg pairs: Pair<String, Any>) =
    sendMessage(tl(key, player, *pairs))

fun broadcast(message: String, vararg pairs: Pair<String, Any>) =
    Bukkit.broadcastMessage(format(message, *pairs))

fun broadcast(message: String, player: Player, vararg pairs: Pair<String, Any>) =
    Bukkit.broadcastMessage(format(message, player, *pairs))

fun broadcastTl(key: String, vararg pairs: Pair<String, Any>) =
    Bukkit.broadcastMessage(tl(key, *pairs))

fun broadcastTl(key: String, player: Player, vararg pairs: Pair<String, Any>) =
    Bukkit.broadcastMessage(tl(key, player, *pairs))

fun getMessage(key: String): String = bundle.getString(key)

fun tl(key: String, vararg pairs: Pair<String, Any>): String =
    format(getMessage(key), *pairs)

fun tl(key: String, player: Player, vararg pairs: Pair<String, Any>): String =
    tl(key, *pairs, "name" to player.name, "displayname" to player.displayName)

fun tlError(key: String, vararg pairs: Pair<String, Any>): String =
    tl("errorMessage", "message" to tl(key, *pairs))

fun format(string: String, player: Player, vararg pairs: Pair<String, Any>): String =
    format(string, "name" to player.name, "displayname" to player.displayName, *pairs)

fun format(string: String, vararg pairs: Pair<String, Any>): String {
    var message = colorize(string)
    for (pair in pairs) {
        message = message.replace("{${pair.first.uppercase()}}", pair.second.toString())
    }
    return message
}