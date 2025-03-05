@file:JvmName("MessageUtils")

package me.zavdav.zcore.util

import me.zavdav.zcore.config.Config
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.util.ResourceBundle

private val bundle: ResourceBundle = ResourceBundle.getBundle("messages")

fun CommandSender.send(message: String, vararg pairs: Pair<String, Any>) =
    sendMessage(format(message, *pairs))

fun CommandSender.sendTl(key: String, vararg args: Any) =
    sendMessage(tl(key, *args))

fun broadcast(message: String, vararg pairs: Pair<String, Any>) =
    Bukkit.broadcastMessage(format(message, *pairs))

fun broadcastTl(key: String, vararg args: Any) =
    Bukkit.broadcastMessage(tl(key, *args))

fun getMessage(key: String): String = bundle.getString(key)

fun tl(key: String, vararg args: Any): String {
    var message = colorize(getMessage(key))
    message = message.replace("{$}", colorize(Config.prefix))
    message = message.replace("{!}", colorize(Config.errorPrefix))

    for (i in args.indices) {
        message = message.replace("{$i}", args[i].toString())
    }
    return message
}

fun format(string: String, vararg pairs: Pair<String, Any>): String {
    var message = colorize(string)
    for (pair in pairs) {
        message = message.replace("{${pair.first.uppercase()}}", pair.second.toString())
    }
    return message
}

fun colorize(message: String) = message.replace("&([0-9a-f])".toRegex(), "§$1")

fun joinArgs(list: List<Any>, fromIndex: Int, toIndex: Int, delimiter: String) =
    list.subList(fromIndex, toIndex).joinToString(delimiter)

fun joinArgs(list: List<Any>, fromIndex: Int, toIndex: Int) =
    joinArgs(list, fromIndex, toIndex, " ")

fun joinArgs(list: List<Any>, fromIndex: Int, delimiter: String) =
    joinArgs(list, fromIndex, list.size, delimiter)

fun joinArgs(list: List<Any>, fromIndex: Int) =
    joinArgs(list, fromIndex, list.size)