package me.zavdav.zcore.data

import com.github.cliftonlabs.json_simple.JsonException
import com.github.cliftonlabs.json_simple.JsonObject
import com.github.cliftonlabs.json_simple.Jsoner
import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.Logger
import me.zavdav.zcore.util.asyncDelayedTask
import java.io.File
import java.io.FileReader
import java.nio.file.Files

abstract class JsonData(filePath: String) {

    protected val file: File = File(ZCore.dataFolder, filePath)
    protected var json: JsonObject = JsonObject()
    protected var hashCode: Int = -1

    init {
        if (!file.exists()) {
            file.parentFile.mkdirs()
        } else {
            try {
                json = Jsoner.deserialize(FileReader(file)) as JsonObject
            } catch (e: JsonException) {
                Logger.severe("Failed to parse data in ${file.name}, resetting data.")
                e.printStackTrace()
            }
        }
    }

    abstract fun deserialize()

    abstract fun serialize()

    fun saveData(async: Boolean, force: Boolean = false, file: File = this.file) {
        serialize()
        if (!force) {
            if (hashCode == json.hashCode()) return
            hashCode = json.hashCode()
        }

        val json = json.toJson()
        val write = { Files.write(file.toPath(), Jsoner.prettyPrint(json).encodeToByteArray()) }
        if (async) asyncDelayedTask { write() } else write()
    }
}