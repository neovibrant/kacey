package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.NoExtraProps.noExtraProps
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Nothing.nothing
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

typealias Prop = Map<String, Any?>

val Prop.id: String
    get() = this["id"] as String


class ApiJsonMatcher {
    private val fields: MutableMap<String, Any?> = mutableMapOf()

    infix fun String.To(that: Any?) {
        fields[this] = that
    }

    fun noExtraProps() {
        val (key, value) = noExtraProps
        fields[key] = value
    }

    companion object {
        fun json(block: ApiJsonMatcher.() -> Unit): Prop {
            val mapBuilder = ApiJsonMatcher()
            mapBuilder.block()
            return mapBuilder.fields.toMap()
        }

        fun containsProp(expected: Prop): Matcher<Prop?> {
            return object : BaseMatcher<Prop>() {
                var matchResult: PropMatchResult? = null

                override fun describeTo(description: Description?) {
                    if (matchResult != null) {
                        description
                            ?.appendText("to match expectation, but instead:\n\n***** Failure detail *****\n")
                        if (matchResult?.noExtraOptionsFailure == true) {
                            description
                                ?.appendText("\tMatching failed due to EXTRA PROP(s) at path: ")
                                ?.appendValue(matchResult?.options?.path?.takeIf { it.isNotBlank() }
                                    ?: "(root of object)")
                                ?.appendText("\n\tExtra properties: ")
                                ?.appendValue(matchResult?.actual)
                                ?.appendText("\n")
                        } else if (matchResult?.arraySizeMismatch == true) {
                            description
                                ?.appendText("\tMatching failed due to mis-matching array SIZE at path: ")
                                ?.appendValue(matchResult?.options?.path?.takeIf { it.isNotBlank() }
                                    ?: "(root of object)")
                                ?.appendText("\n\tExpected size: ")
                                ?.appendValue(matchResult?.expected)
                                ?.appendText("\n\tActual size: ")
                                ?.appendValue(matchResult?.actual)
                                ?.appendText("\n")
                        } else {
                            description
                                ?.appendText("\tMatching failed for key: ")
                                ?.appendValue(matchResult?.options?.path)
                                ?.appendText("\n\tExpected value: ")
                                ?.appendValue(matchResult?.expected)
                                ?.appendText("\n\tActual value: ")
                                ?.appendValue(matchResult?.actual)
                                ?.appendText("\n")
                        }
                        description
                            ?.appendText("***** End of detail *****\n")
                    }
                    description
                            ?.appendText("\nFull expectation: ")
                            ?.appendValue(expected)
                }

                override fun matches(actualValue: Any?): Boolean {
                    @Suppress("UNCHECKED_CAST")
                    val actual = actualValue as? Prop
                    val matchResult = PropMatching().match(actual, expected)
                    this.matchResult = matchResult
                    return matchResult.matches
                }
            }
        }

        fun contains(vararg expected: Prop): Matcher<List<Prop>?> {
            return object : BaseMatcher<List<Prop>>() {
                private var orderMismatch = false

                override fun describeTo(description: Description?) {
                    description
                            ?.appendValue(expected)
                            ?.appendText("\nto contain all in order.\n ")
                }

                override fun matches(actualValue: Any?): Boolean {
                    @Suppress("UNCHECKED_CAST")
                    val actual = actualValue as? List<Prop>

                    val expectedAndActualHaveSameSize = (actual?.size ?: 0) == expected.size
                    val allMatchInOrder = {
                        actual
                                ?.mapIndexed { index, actualProp ->
                                    val expectedProp = expected[index]
                                    actualProp.matches(expectedProp)
                                }
                                ?.all { it }
                                ?: false
                    }
                    val result = expectedAndActualHaveSameSize && allMatchInOrder()
                    if (!result) {
                        orderMismatch = allMatchIgnoringOrder(actualValue, expected)
                    }
                    return result
                }

                override fun describeMismatch(item: Any?, description: Description?) {
                    if (orderMismatch) {
                        description?.appendText("although they were equal, they were in the WRONG ORDER: ")?.appendValue(item)
                    } else {
                        super.describeMismatch(item, description)
                    }
                    orderMismatch = false
                }
            }
        }

        fun containsAtLeast(vararg expected: Prop): Matcher<List<Prop>?> {
            return object : BaseMatcher<List<Prop>>() {
                override fun describeTo(description: Description?) {
                    description
                            ?.appendText("\nto contain some:\n ")
                            ?.appendValue(expected)
                }

                override fun matches(actualValue: Any?): Boolean {
                    @Suppress("UNCHECKED_CAST")
                    val actual = actualValue as? List<Prop>
                    return expected.all { expectedProp ->
                        actual?.any { actualProp ->
                            actualProp.matches(expectedProp)
                        } ?: false
                    }
                }
            }
        }

        fun containsInAnyOrder(vararg expected: Prop): Matcher<List<Prop>?> {
            return object : BaseMatcher<List<Prop>>() {
                override fun describeTo(description: Description?) {
                    description
                            ?.appendText("\nto contain all:\n ")
                            ?.appendValue(expected)
                }

                override fun matches(actualValue: Any?): Boolean {
                    return allMatchIgnoringOrder(actualValue, expected)
                }
            }
        }

        private fun allMatchIgnoringOrder(actualValue: Any?, expected: Array<out Prop>): Boolean {
            @Suppress("UNCHECKED_CAST")
            val actual = actualValue as? List<Prop>
            val eachActualMatchesAnExpected = actual?.all { actualProp ->
                expected.any { expectedProp ->
                    actualProp.matches(expectedProp)
                }
            } ?: false
            val eachExpectedMatchesAnActual = expected.all { expectedProp ->
                actual?.any { actualProp ->
                    actualProp.matches(expectedProp)
                } ?: false
            }
            val expectedAndActualHaveSameSize = (actual?.size ?: 0) == expected.size

            return eachActualMatchesAnExpected
                    && eachExpectedMatchesAnActual
                    && expectedAndActualHaveSameSize
        }

        fun isEmpty(): Matcher<List<Prop>?> {
            return contains()
        }

        private fun Prop.matches(expected: Prop, path: String? = null, checkNoExtraProps: Boolean = false): Boolean {
            val noExtraPropsExpected = checkNoExtraProps || expected.keys.contains(noExtraProps.first)
            val allExpectedMatch = expected
                    .map { (expectedKey, expectedValue) ->
                        val actualValue = this[expectedKey]
                        val keyPath = "${if (path == null) "" else "$path > "}$expectedKey"
                        val matches = when (expectedValue) {
                            is Something -> actualValue != null
                            is Nothing -> actualValue == null
                            noExtraProps.second -> true
                            is Map<*, *> -> {
                                @Suppress("UNCHECKED_CAST")
                                val actualProp = (actualValue as? Prop) ?: mapOf()
                                @Suppress("UNCHECKED_CAST")
                                val expectedProp = expectedValue as Prop
                                val matchingMap = actualProp.matches(expectedProp, keyPath, noExtraPropsExpected)
                                matchingMap
                            }
                            is List<*> -> {
                                val listOfProps = expectedValue.all { it is Map<*, *> }
                                if (listOfProps) {
                                    @Suppress("UNCHECKED_CAST")
                                    val expectedValueList = expectedValue as List<Prop>
                                    @Suppress("UNCHECKED_CAST")
                                    val actualValueList = actualValue as? List<*>
                                    contains(*expectedValueList.toTypedArray()).matches(actualValueList)
                                } else {
                                    actualValue == expectedValue
                                }
                            }
                            else -> actualValue == expectedValue
                        }
                        if (!matches) {
                            System.err.println("Assertion failed for '${keyPath}': '${actualValue}' did not equal '${expectedValue}'")
                        }
                        matches
                    }
                    .all { it }

            val sizeMatchesIfRequired = if (allExpectedMatch && noExtraPropsExpected) {
                this.keys.size == expected.keys
                        .asSequence()
                        .filter { it != noExtraProps.first }
                        .filter { expected[it] != nothing }
                        .count()
            } else {
                true
            }

            return allExpectedMatch && sizeMatchesIfRequired
        }
    }

    object NoExtraProps {
        val noExtraProps = Pair("__NO_EXTRA_PROPS__", "__NO_EXTRA_PROPS__")
    }

    object Something {
        val something = Something

        override fun toString(): String {
            return "something"
        }
    }

    object Nothing {
        val nothing = Nothing

        override fun toString(): String {
            return "nothing"
        }
    }
}
