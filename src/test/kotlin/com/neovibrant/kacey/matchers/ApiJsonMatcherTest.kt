package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.containsProp
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Something.something
import org.junit.Assert.*
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

    @Test
    fun `when a prop fails to match`() {
        assertThat(json {
            "id" To "123"
            "nested object" To json {
                "name" To "Jim"
                "location" To "Up north"
            }
        }, containsProp(json {
            "id" To something
            "nested object" To json {
                "name" To "Jim"
                "location" To "Down south"
            }
        }))
    }
}
