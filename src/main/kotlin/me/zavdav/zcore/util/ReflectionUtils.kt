package me.zavdav.zcore.util

@Suppress("UNCHECKED_CAST")
fun <R> getField(obj: Any, name: String): R? {
    val field = obj::class.java.getDeclaredField(name)
    field.isAccessible = true
    val ret = field.get(obj) as? R
    field.isAccessible = false
    return ret
}