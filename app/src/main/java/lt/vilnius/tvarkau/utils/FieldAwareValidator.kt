package lt.vilnius.tvarkau.utils

import rx.Single
import rx.Single.defer
import rx.Single.just

/**
 * @author Martynas Jurkus
 */
class FieldAwareValidator<T> private constructor(
        private val obj: T,
        private val exception: ValidationException? = null
) {

    private var nested: FieldAwareValidator<*>? = null

    fun validate(predicate: (T) -> Boolean, viewId: Int, message: String): FieldAwareValidator<T> {
        if (exception == null && !predicate(obj)) {
            return FieldAwareValidator(obj, ValidationException(viewId, message))
        }

        return this
    }

    fun <E> validate(validator: FieldAwareValidator<E>): FieldAwareValidator<T> {
        nested = validator

        return this
    }

    /**
     * Will throw [ValidationException] if form data is not valid
     */
    fun get(): T {
        val result = if (exception == null) {
            obj
        } else {
            throw exception
        }

        nested?.get()

        return result
    }

    fun toSingle(): Single<T> {
        return defer { just(get()) }
    }

    companion object {
        fun <T> of(obj: T): FieldAwareValidator<T> {
            return FieldAwareValidator(obj);
        }
    }

    class ValidationException(val viewId: Int, message: String) : IllegalStateException(message)
}