package org.betamc.core.data

import com.github.cliftonlabs.json_simple.JsonException
import com.github.cliftonlabs.json_simple.JsonObject
import com.github.cliftonlabs.json_simple.Jsoner
import org.betamc.core.BMCCore
import java.io.File
import java.io.FileReader
import java.io.FileWriter

abstract class JsonData(private val file: File) {

    protected var json: JsonObject = JsonObject()
    private var hashCode: Int = json.hashCode()
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
                BMCCore.logger.severe("${BMCCore.prefix} FAILED TO PARSE DATA IN ${file.name}, RESETTING DATA")
                e.printStackTrace()
                initialize = true
            }
        }
    }

    fun saveData() {
        if (hashCode == json.hashCode()) return
        hashCode = json.hashCode()
        FileWriter(file).use { file ->
            file.write(Jsoner.prettyPrint(json.toJson()))
            file.flush()
        }
    }
}