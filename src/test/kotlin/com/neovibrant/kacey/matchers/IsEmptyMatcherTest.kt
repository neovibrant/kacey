package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.Assertion.Companion.assertion
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.isEmpty
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import org.junit.Assert.assertThat
import org.junit.Test

class IsEmptyMatcherTest {
    @Test
    fun `is empty when it contains nothing`() {
        assertThat(listOf(), isEmpty())
    }

    @Test
    fun `is empty - contains items`() {
        assertion {
            assertThat(
                listOf(
                    json {
                        "id" To 123
                        "name" To "Mary"
                    },
                    json {
                        "id" To 456
                        "name" To "Jake"
                    },
                ),
                isEmpty(),
            )
        }.failsWith(
            "Matching failed due to mis-matching array SIZE at path: \"(root of object)\"",
            "Expected size: <0>",
            "Actual size: <2>",
        )
    }
}
