package me.zavdav.zcore.hooks.permissions

import com.johnymuffin.jperms.beta.JohnyPerms
import com.johnymuffin.jperms.beta.JohnyPermsAPI
import me.zavdav.zcore.util.colorize
import org.bukkit.entity.Player
import java.util.UUID

class JohnyPermsHook : PermissionHook {

    private val api: JohnyPermsAPI = JohnyPerms.getJPermsAPI()

    override fun hasPermission(player: Player, permission: String): Boolean =
        player.isOp || api.getUser(player.uniqueId).hasPermission(permission)

    override fun getPrefix(uuid: UUID): String =
        colorize(api.getUser(uuid).prefix ?: api.getUser(uuid).group.prefix ?: "")

    override fun getSuffix(uuid: UUID): String =
        colorize(api.getUser(uuid).suffix ?: api.getUser(uuid).group.suffix ?: "")
}