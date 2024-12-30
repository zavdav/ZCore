package org.poseidonplugins.zcore.permissions

import com.johnymuffin.jperms.beta.JohnyPerms
import com.johnymuffin.jperms.beta.JohnyPermsAPI
import org.poseidonplugins.commandapi.colorize
import java.util.UUID

class JohnyPermsHandler : IPermissionHandler {
    val api: JohnyPermsAPI = JohnyPerms.getJPermsAPI()

    override fun getPrefix(uuid: UUID): String =
        colorize(api.getUser(uuid).prefix ?: api.getUser(uuid).group.prefix ?: "")

    override fun getSuffix(uuid: UUID): String =
        colorize(api.getUser(uuid).suffix ?: api.getUser(uuid).group.suffix ?: "")
}