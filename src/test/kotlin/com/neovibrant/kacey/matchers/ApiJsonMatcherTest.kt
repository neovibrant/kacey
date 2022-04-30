package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.Assertion.Companion.assertion
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.containsProp
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Nothing.nothing
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

    @Test
    fun `explanatory error message - root property`() {
        assertion {
            assertThat(json {
                "id" To "123"
                "name" To "Jim"
                "nested object" To json {
                    "location" To "Up north"
                }
            }, containsProp(json {
                "id" To something
                "name" To "Jane"
                "nested object" To json {
                    "location" To "Up north"
                }
            }))
        }.failsWith(
            "Matching failed for key: \"name\"",
            "Expected value: \"Jane\"",
            "Actual value: \"Jim\""
        )
    }

    @Test
    fun `explanatory error message - nested object`() {
        assertion {
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
        }.failsWith(
            "Matching failed for key: \"'nested object'.location\"",
            "Expected value: \"Down south\"",
            "Actual value: \"Up north\""
        )
    }

    @Test
    fun `explanatory error message - unexpected field`() {
        assertion {
            assertThat(json {
                "unexpected" To "123"
            }, containsProp(json {
                "unexpected" To nothing
            }))
        }.failsWith(
            "Matching failed for key: \"unexpected\"",
            "Expected value: <nothing>",
            "Actual value: \"123\""
        )
    }

//    @Test
//    fun `explanatory error message - extra props`() {
//        assertion {
//            assertThat(json {
//                "id" To "123"
//                "expectedExtra" To "extra"
//            }, containsProp(json {
//                "id" To something
//                noExtraProps()
//            }))
//        }.failsWith(
//            "Matching failed for key: \"unexpected\"",
//            "Expected value: <nothing>",
//            "Actual value: \"123\""
//        )
//    }
}
