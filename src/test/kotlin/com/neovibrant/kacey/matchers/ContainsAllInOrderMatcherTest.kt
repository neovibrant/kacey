package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.contains
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain

class ContainsAllInOrderMatcherTest : FunSpec({
    test("contains all in order") {
        listOf(
            json {
                "id" To 123
            },
            json {
                "id" To 456
            },
        ) should contains(
            json {
                "id" To 123
            },
            json {
                "id" To 456
            },
        )
    }

    test("contains all in order - missing item") {
        val exception = shouldThrow<AssertionError> {
            listOf(
                json {
                    "id" To 123
                },
            ) should contains(
                json {
                    "id" To 123
                },
                json {
                    "id" To 456
                },
            )
        }

        exception.message shouldContain "Matching failed due to mis-matching array SIZE at path: \"(root of object)\""
        exception.message shouldContain "Expected size: <2>"
        exception.message shouldContain "Actual size: <1>"
    }

    test("contains all in order - property not matching") {
        val exception = shouldThrow<AssertionError> {
            listOf(
                json {
                    "id" To 123
                    "name" To "Mary"
                },
                json {
                    "id" To 456
                    "name" To "John"
                },
            ) should contains(
                json {
                    "name" To "Mary"
                },
                json {
                    "name" To "Jake"
                },
            )
        }

        exception.message shouldContain "Matching failed for key: \"[1].name\""
        exception.message shouldContain "Expected value: \"Jake\""
        exception.message shouldContain "Actual value: \"John\""
    }

    test("contains all in order - incorrect order") {
        val exception = shouldThrow<AssertionError> {
            listOf(
                json {
                    "id" To 123
                    "name" To "Mary"
                },
                json {
                    "id" To 456
                    "name" To "John"
                },
            ) should contains(
                json {
                    "name" To "John"
                },
                json {
                    "name" To "Mary"
                },
            )
        }

        exception.message shouldContain "Matching failed due to WRONG ORDER of values at path: \"(root of object)\""
    }
})
