package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.Assertion.Companion.assertion
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.containsInAnyOrder
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import org.junit.Assert.assertThat
import org.junit.Test

class ContainsAllInAnyOrderMatcherTest {
    @Test
    fun `contains all in any order - incorrect order`() {
        assertThat(listOf(json {
            "id" To 123
            "name" To "Mary"
        }, json {
            "id" To 456
            "name" To "John"
        }), containsInAnyOrder(json {
            "name" To "John"
        }, json {
            "name" To "Mary"
        }))
    }

    @Test
    fun `contains all in any order - incorrect value`() {
        assertion {
            assertThat(listOf(json {
                "id" To 123
                "name" To "Mary"
            }, json {
                "id" To 456
                "name" To "Jake"
            }), containsInAnyOrder(json {
                "name" To "John"
            }, json {
                "name" To "Mary"
            }))
        }.failsWith(
            "Matching failed for key: \"[0]\"",
            "Expected value: <{name=John}>",
            "Actual value: \"<Nothing that matched in any order>\""
        )
    }

    @Test
    fun `contains all in any order - missing item`() {
        assertion {
            assertThat(listOf(json {
                "id" To 123
                "name" To "Mary"
            }, json {
                "id" To 456
                "name" To "Jake"
            }), containsInAnyOrder(json {
                "name" To "Mary"
            }))
        }.failsWith(
            "Matching failed due to mis-matching array SIZE at path: \"(root of object)\"",
            "Expected size: <1>",
            "Actual size: <2>"
        )
    }
}
