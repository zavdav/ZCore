package org.poseidonplugins.zcore.data

import com.github.cliftonlabs.json_simple.JsonException
import com.github.cliftonlabs.json_simple.JsonObject
import com.github.cliftonlabs.json_simple.Jsoner
import org.poseidonplugins.zcore.ZCore
import java.io.File
import java.io.FileReader
import java.io.FileWriter

abstract class JsonData(private val file: File) {

    protected var json: JsonObject = JsonObject()
    private var hashCode: Int = -1
    protected var initialize = false

    init {
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
            initialize = true
        } else {
            try {
                json = Jsoner.deserialize(FileReader(file)) as JsonObject
            } catch (e: JsonException) {
                ZCore.logger.severe("${ZCore.prefix} FAILED TO PARSE DATA IN ${file.name}, RESETTING DATA")
                e.printStackTrace()
                initialize = true
            }
        }
    }

    @Synchronized
    fun saveData() {
        if (hashCode == json.hashCode()) return
        hashCode = json.hashCode()
        FileWriter(file).use { file ->
            file.write(Jsoner.prettyPrint(json.toJson()))
            file.flush()
        }
    }
}