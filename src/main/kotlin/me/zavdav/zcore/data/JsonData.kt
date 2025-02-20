package me.zavdav.zcore.data

import com.github.cliftonlabs.json_simple.JsonException
import com.github.cliftonlabs.json_simple.JsonObject
import com.github.cliftonlabs.json_simple.Jsoner
import me.zavdav.zcore.util.Logger
import java.io.File
import java.io.FileReader
import java.io.FileWriter

abstract class JsonData(protected val file: File) {

    protected var json: JsonObject = JsonObject()
    protected var hashCode: Int = -1
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
                Logger.severe("Failed to parse data in ${file.name}, resetting data.")
                e.printStackTrace()
                initialize = true
            }
        }
    }

    @Synchronized
    open fun saveData() {
        if (hashCode == json.hashCode()) return
        hashCode = json.hashCode()
        saveTo(file)
    }

    open fun saveTo(file: File) {
        FileWriter(file).use { fw ->
            fw.write(Jsoner.prettyPrint(json.toJson()))
            fw.flush()
        }
    }

    inline operator fun <reified T> JsonObject.get(key: String, def: T): T =
        when (def) {
            is Int -> (this[key] as? Number ?: def).toInt() as T
            is Long -> (this[key] as? Number ?: def).toLong() as T
            is Double -> (this[key] as? Number ?: def).toDouble() as T
            else -> (this[key] ?: def) as T
        }
}