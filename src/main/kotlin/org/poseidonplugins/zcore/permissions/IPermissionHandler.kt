package org.poseidonplugins.zcore.permissions

import java.util.UUID

interface IPermissionHandler {

    fun getPrefix(uuid: UUID): String

    fun getSuffix(uuid: UUID): String
}