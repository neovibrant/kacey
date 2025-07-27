package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.containsProp
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Companion.json
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Nothing.nothing
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Something.something
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.shouldContain

class ContainsPropMatcherTest : FunSpec({
    test("explanatory error message - root property") {
        val exception = shouldThrow<AssertionError> {
            json {
                "id" To "123"
                "name" To "Jim"
                "nested object" To
                    json {
                        "location" To "Up north"
                    }
            } should containsProp(
                json {
                    "id" To something
                    "name" To "Jane"
                    "nested object" To
                        json {
                            "location" To "Up north"
                        }
                },
            )
        }

        exception.message shouldContain "Matching failed for key: \"name\""
        exception.message shouldContain "Expected value: \"Jane\""
        exception.message shouldContain "Actual value: \"Jim\""
    }

    test("explanatory error message - nested object") {
        val exception = shouldThrow<AssertionError> {
            json {
                "id" To "123"
                "nested object" To
                    json {
                        "name" To "Jim"
                        "location" To "Up north"
                    }
            } should containsProp(
                json {
                    "id" To something
                    "nested object" To
                        json {
                            "name" To "Jim"
                            "location" To "Down south"
                        }
                },
            )
        }

        exception.message shouldContain "Matching failed for key: \"'nested object'.location\""
        exception.message shouldContain "Expected value: \"Down south\""
        exception.message shouldContain "Actual value: \"Up north\""
    }

    test("explanatory error message - unexpected field") {
        val exception = shouldThrow<AssertionError> {
            json {
                "unexpected" To "123"
            } should containsProp(
                json {
                    "unexpected" To nothing
                },
            )
        }

        exception.message shouldContain "Matching failed for key: \"unexpected\""
        exception.message shouldContain "Expected value: \"nothing\""
        exception.message shouldContain "Actual value: \"123\""
    }

    test("explanatory error message - extra props") {
        val exception = shouldThrow<AssertionError> {
            json {
                "id" To "123"
                "expectedExtra" To "extra"
            } should containsProp(
                json {
                    "id" To something
                    noExtraProps()
                },
            )
        }

        exception.message shouldContain "Matching failed due to EXTRA PROP(s) at path: \"(root of object)\""
        exception.message shouldContain "Extra properties: [expectedExtra]"
    }

    test("explanatory error message - extra props is nested") {
        val exception = shouldThrow<AssertionError> {
            json {
                "id" To "123"
                "nestedObject" To
                    json {
                        "expected" To 1
                        "expectedExtra" To "extra"
                    }
            } should containsProp(
                json {
                    "id" To something
                    "nestedObject" To
                        json {
                            "expected" To 1
                        }
                    noExtraProps()
                },
            )
        }

        exception.message shouldContain "Matching failed due to EXTRA PROP(s) at path: \"nestedObject\""
        exception.message shouldContain "Extra properties: [expectedExtra]"
    }

    test("explanatory error message - nested array") {
        val exception = shouldThrow<AssertionError> {
            json {
                "id" To "123"
                "nested array" To
                    listOf(
                        json {
                            "name" To "Marry"
                            "location" To "West"
                        },
                        json {
                            "name" To "Jim"
                            "location" To "Up north"
                        },
                    )
            } should containsProp(
                json {
                    "id" To something
                    "nested array" To
                        listOf(
                            json {
                                "name" To "Marry"
                                "location" To "West"
                            },
                            json {
                                "name" To "Jim"
                                "location" To "Down south"
                            },
                        )
                },
            )
        }

        exception.message shouldContain "Matching failed for key: \"'nested array'.[1].location\""
        exception.message shouldContain "Expected value: \"Down south\""
        exception.message shouldContain "Actual value: \"Up north\""
    }

    test("explanatory error message - nested array incomplete") {
        val exception = shouldThrow<AssertionError> {
            json {
                "id" To "123"
                "nested array" To
                    listOf(
                        json {
                            "name" To "Marry"
                            "location" To "West"
                        },
                    )
            } should containsProp(
                json {
                    "id" To something
                    "nested array" To
                        listOf(
                            json {
                                "name" To "Marry"
                                "location" To "West"
                            },
                            json {
                                "name" To "Jim"
                                "location" To "Down south"
                            },
                        )
                },
            )
        }

        exception.message shouldContain "Matching failed due to mis-matching array SIZE at path: \"'nested array'\""
        exception.message shouldContain "Expected size: <2>"
        exception.message shouldContain "Actual size: <1>"
    }

    test("explanatory error message - nested array is null") {
        val exception = shouldThrow<AssertionError> {
            json {
                "id" To "123"
            } should containsProp(
                json {
                    "id" To something
                    "nonExistingArray" To
                        listOf(
                            json {
                                "name" To "Marry"
                                "location" To "West"
                            },
                            json {
                                "name" To "Jim"
                                "location" To "Down south"
                            },
                        )
                },
            )
        }

        exception.message shouldContain "Matching failed for key: \"nonExistingArray\""
        exception.message shouldContain "Expected value: \"[{name=Marry, location=West}, {name=Jim, location=Down south}]\""
        exception.message shouldContain "Actual value: \"null\""
    }
})
