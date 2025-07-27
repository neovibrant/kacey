package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.containsInAnyOrder
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain

class ContainsAllInAnyOrderMatcherTest : FunSpec({
    test("contains all in any order - incorrect order") {
        listOf(
            json {
                "id" To 123
                "name" To "Mary"
            },
            json {
                "id" To 456
                "name" To "John"
            },
        ) should containsInAnyOrder(
            json {
                "name" To "John"
            },
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
            ) should containsInAnyOrder(
                json {
                    "name" To "John"
                },
                json {
                    "name" To "Mary"
                },
            )
        }

        exception.message shouldContain "Matching failed for key: \"[0]\""
        exception.message shouldContain "Expected value: \"{name=John}\""
        exception.message shouldContain "Actual value: \"<Nothing that matched in any order>\""
    }

    test("contains all in any order - missing item") {
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
            ) should containsInAnyOrder(
                json {
                    "name" To "Mary"
                },
            )
        }

        exception.message shouldContain "Matching failed due to mis-matching array SIZE at path: \"(root of object)\""
        exception.message shouldContain "Expected size: <1>"
        exception.message shouldContain "Actual size: <2>"
    }
})
