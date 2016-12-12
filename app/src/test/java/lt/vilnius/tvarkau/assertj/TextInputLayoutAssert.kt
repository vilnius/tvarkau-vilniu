package lt.vilnius.tvarkau.assertj

import android.support.design.widget.TextInputLayout

import org.assertj.android.api.widget.AbstractLinearLayoutAssert

/**
 * @author Martynas Jurkus
 */
class TextInputLayoutAssert private constructor(actual: TextInputLayout) :
        AbstractLinearLayoutAssert<TextInputLayoutAssert, TextInputLayout>(actual, TextInputLayoutAssert::class.java) {

    fun noError(): TextInputLayoutAssert {
        val actualError = actual.error.emptyToNull()

        org.assertj.core.api.Assertions.assertThat(actualError)
                .overridingErrorMessage("Expected no error but was <%s>.", actualError)
                .isNull()

        return this
    }

    fun hasError(stringRes: Int): TextInputLayoutAssert {
        return hasError(actual.context.getString(stringRes))
    }

    fun hasError(errorText: String): TextInputLayoutAssert {

        val actualError = actual.error

        org.assertj.core.api.Assertions.assertThat(actualError)
                .overridingErrorMessage("Expected error text <%s> but was <%s>.", errorText, actualError)
                .isEqualTo(errorText)

        return this
    }

    companion object {
        fun assertThat(actual: TextInputLayout): TextInputLayoutAssert {
            return TextInputLayoutAssert(actual)
        }
    }

    private fun CharSequence?.emptyToNull(): CharSequence? {
        return if (this.isNullOrEmpty()) null else this
    }
}
