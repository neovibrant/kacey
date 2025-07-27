package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.isEmpty
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain

class IsEmptyMatcherTest : FunSpec({
    test("is empty when it contains nothing") {
        listOf<Prop>() should isEmpty()
    }

    test("is empty - contains items") {
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
            ) should isEmpty()
        }

        exception.message shouldContain "Matching failed due to mis-matching array SIZE at path: \"(root of object)\""
        exception.message shouldContain "Expected size: <0>"
        exception.message shouldContain "Actual size: <2>"
    }
})
