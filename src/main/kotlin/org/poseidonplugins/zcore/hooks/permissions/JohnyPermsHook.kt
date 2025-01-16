package org.poseidonplugins.zcore.hooks.permissions

import com.johnymuffin.jperms.beta.JohnyPerms
import com.johnymuffin.jperms.beta.JohnyPermsAPI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.colorize
import java.util.UUID

class JohnyPermsHook : PermissionHook {

    private val api: JohnyPermsAPI = JohnyPerms.getJPermsAPI()

    override fun hasPermission(sender: CommandSender, permission: String): Boolean {
        if (sender !is Player) return true
        return sender.isOp || api.getUser(sender.uniqueId).hasPermission(permission)
    }

    override fun getPrefix(uuid: UUID): String =
        colorize(api.getUser(uuid).prefix ?: api.getUser(uuid).group.prefix ?: "")

    override fun getSuffix(uuid: UUID): String =
        colorize(api.getUser(uuid).suffix ?: api.getUser(uuid).group.suffix ?: "")
}