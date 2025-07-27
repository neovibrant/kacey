package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.containsProp
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Something.something
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should

class ApiJsonMatcherTest : FunSpec({
    test("prop is matched by properties") {
        val prop: Prop = mapOf("id" to 123, "name" to "jim")
        prop should containsProp(
            json {
                "id" To something
                "name" To "jim"
            },
        )
    }
})
