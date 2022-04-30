package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.containsProp
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Something.something
import org.junit.Assert.assertThat
import org.junit.Test

class ApiJsonMatcherTest {
    @Test
    fun `prop is matched by properties`() {
        val prop: Prop = mapOf("id" to 123, "name" to "jim")
        assertThat(prop, containsProp(json {
            "id" To something
            "name" To "jim"
        }))
    }
}
