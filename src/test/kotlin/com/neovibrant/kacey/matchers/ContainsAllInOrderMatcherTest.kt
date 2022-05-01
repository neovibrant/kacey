package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.Assertion.Companion.assertion
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.contains
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import org.junit.Assert.assertThat
import org.junit.Test

class ContainsAllInOrderMatcherTest {
    @Test
    fun `contains all in order`() {
        assertThat(listOf(json {
            "id" To 123
        }, json {
            "id" To 456
        }), contains(json {
            "id" To 123
        }, json {
            "id" To 456
        }))
    }

    @Test
    fun `contains all in order - missing item`() {
        assertion {
            assertThat(listOf(json {
                "id" To 123
            }), contains(json {
                "id" To 123
            }, json {
                "id" To 456
            }))
        }.failsWith(
            "Matching failed due to mis-matching array SIZE at path: \"(root of object)\"",
            "Expected size: <2>",
            "Actual size: <1>"
        )
    }

    @Test
    fun `contains all in order - property not matching`() {
        assertion {
            assertThat(listOf(json {
                "id" To 123
                "name" To "Mary"
            }, json {
                "id" To 456
                "name" To "John"
            }), contains(json {
                "name" To "Mary"
            }, json {
                "name" To "Jake"
            }))
        }.failsWith(
            "Matching failed for key: \"[1].name\"",
            "Expected value: \"Jake\"",
            "Actual value: \"John\""
        )
    }

    @Test
    fun `contains all in order - incorrect order`() {
        assertion {
            assertThat(listOf(json {
                "id" To 123
                "name" To "Mary"
            }, json {
                "id" To 456
                "name" To "John"
            }), contains(json {
                "name" To "John"
            }, json {
                "name" To "Mary"
            }))
        }.failsWith(
            "Matching failed due to WRONG ORDER of values at path: \"(root of object)\""
        )
    }
}
