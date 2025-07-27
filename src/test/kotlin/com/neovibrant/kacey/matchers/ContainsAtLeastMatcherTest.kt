package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.containsAtLeast
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain

class ContainsAtLeastMatcherTest : FunSpec({
    test("contains all in any order") {
        listOf(
            json {
                "id" To 123
                "name" To "Mary"
            },
            json {
                "id" To 456
                "name" To "John"
            },
        ) should containsAtLeast(
            json {
                "name" To "John"
            },
            json {
                "name" To "Mary"
            },
        )
    }

    test("contains all in any order - missing item") {
        listOf(
            json {
                "id" To 123
                "name" To "Mary"
            },
            json {
                "id" To 456
                "name" To "Jake"
            },
        ) should containsAtLeast(
            json {
                "name" To "Mary"
            },
        )
    }

    test("contains all in any order - incorrect value") {
        val exception = shouldThrow<AssertionError> {
            listOf(
                json {
                    "id" To 123
                    "name" To "Mary"
                },
                json {
                    "id" To 456
                    "name" To "Jake"
                },
            ) should containsAtLeast(
                json {
                    "name" To "John"
                },
            )
        }

        exception.message shouldContain "Matching failed for key: \"[0]\""
        exception.message shouldContain "Expected value: \"{name=John}\""
        exception.message shouldContain "Actual value: \"<Nothing that matched in any order>\""
    }
})
