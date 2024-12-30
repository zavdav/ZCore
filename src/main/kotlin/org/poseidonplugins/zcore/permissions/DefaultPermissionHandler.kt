package org.poseidonplugins.zcore.permissions

import java.util.UUID

class DefaultPermissionHandler : IPermissionHandler {

    override fun getPrefix(uuid: UUID): String = ""

    override fun getSuffix(uuid: UUID): String = ""
}