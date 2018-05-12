package lt.vilnius.tvarkau.data

import com.google.gson.Gson
import java.lang.reflect.Type


class GsonSerializerImpl(
        private val gson: Gson
) : GsonSerializer {

    override fun <T> fromJsonType(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }

    override fun <T> toJson(value: T): String {
        return gson.toJson(value)
    }

    override fun <T> fromJson(json: String?, clazz: Class<T>): T {
        return gson.fromJson(json, clazz)
    }
}
