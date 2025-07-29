package com.neovibrant.kacey

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.fail

class Assertion(
    val assertion: () -> Unit,
) {
    fun failsWith(vararg messages: String) {
        assertThat(messages)
            .describedAs("failsWith(..) requires at least one argument")
            .isNotEmpty
        try {
            assertion()
            throw ExpectedAssertionError()
        } catch (e: ExpectedAssertionError) {
            fail("Expected assertion to fail, but there was no failure")
        } catch (e: AssertionError) {
            messages.forEach { message ->
                assertThat(e.localizedMessage).contains(message)
            }
        }
    }

    companion object {
        fun assertion(assertion: () -> Unit) = Assertion(assertion = assertion)
    }

    class ExpectedAssertionError : AssertionError()
}
