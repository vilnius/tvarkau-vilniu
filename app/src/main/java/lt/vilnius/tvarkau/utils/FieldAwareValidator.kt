package lt.vilnius.tvarkau.utils

import io.reactivex.Single
import io.reactivex.Single.defer
import io.reactivex.Single.just

class FieldAwareValidator<T> private constructor(
    private val obj: T,
    private val exception: ValidationException? = null
) {

    fun validate(predicate: (T) -> Boolean, viewId: Int, message: CharSequence): FieldAwareValidator<T> {
        return validate(predicate, viewId, message.toString())
    }

    fun validate(predicate: (T) -> Boolean, viewId: Int, message: String): FieldAwareValidator<T> {
        if (exception == null && !predicate(obj)) {
            return FieldAwareValidator(obj, ValidationException(viewId, message))
        }

        return this
    }

    /**
     * Will throw [ValidationException] if form data is not valid
     */
    fun get(): T {
        return if (exception == null) {
            obj
        } else {
            throw exception
        }
    }

    fun toSingle(): Single<T> {
        return defer { just(get()) }
    }

    companion object {
        fun <T> of(obj: T): FieldAwareValidator<T> {
            return FieldAwareValidator(obj)
        }
    }

    class ValidationException(val viewId: Int, message: String) : IllegalStateException(message)
}
