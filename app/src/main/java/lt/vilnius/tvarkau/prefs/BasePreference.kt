package lt.vilnius.tvarkau.prefs

interface BasePreference<T> {
    fun get(): T
    fun set(value: T)
    fun setSync(value: T)
    fun delete()
    fun isSet(): Boolean
}

