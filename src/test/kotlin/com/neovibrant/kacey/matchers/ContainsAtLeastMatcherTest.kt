package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.Assertion.Companion.assertion
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.containsAtLeast
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class ContainsAtLeastMatcherTest {
    @Test
    fun `contains all in any order`() {
        assertThat(
            listOf(
                json {
                    "id" To 123
                    "name" To "Mary"
                },
                json {
                    "id" To 456
                    "name" To "John"
                },
            ),
            containsAtLeast(
                json {
                    "name" To "John"
                },
                json {
                    "name" To "Mary"
                },
            ),
        )
    }

    @Test
    fun `contains all in any order - missing item`() {
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
            containsAtLeast(
                json {
                    "name" To "Mary"
                },
            ),
        )
    }

    @Test
    fun `contains all in any order - incorrect value`() {
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
                containsAtLeast(
                    json {
                        "name" To "John"
                    },
                ),
            )
        }.failsWith(
            "Matching failed for key: \"[0]\"",
            "Expected value: <{name=John}>",
            "Actual value: \"<Nothing that matched in any order>\"",
        )
    }
}
