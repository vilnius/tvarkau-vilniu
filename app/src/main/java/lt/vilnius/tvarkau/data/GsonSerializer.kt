package lt.vilnius.tvarkau.data

import java.lang.reflect.Type


interface GsonSerializer {
    fun <T> toJson(value: T): String
    fun <T> fromJson(json: String?, clazz: Class<T>): T
    fun <T> fromJsonType(json: String, type: Type): T
}
