package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.NoExtraProps.noExtraProps
import com.neovibrant.kacey.matchers.Descriptioner.describe
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
                    describe(description, expected, matchResult)
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
                var matchResult: PropMatchResult? = null

                override fun describeTo(description: Description?) {
                    describe(description, expected, matchResult)
                }

                override fun matches(actualValue: Any?): Boolean {
                    @Suppress("UNCHECKED_CAST")
                    val actual = actualValue as? List<Prop>
                    val matchResult = PropMatching().containsAllInOrder(actual, expected.toList())
                    this.matchResult = matchResult
                    return matchResult.matches
                }
            }
        }

        fun containsInAnyOrder(vararg expected: Prop): Matcher<List<Prop>?> {
            return object : BaseMatcher<List<Prop>>() {
                var matchResult: PropMatchResult? = null

                override fun describeTo(description: Description?) {
                    describe(description, expected, matchResult)
                }

                override fun matches(actualValue: Any?): Boolean {
                    @Suppress("UNCHECKED_CAST")
                    val actual = actualValue as? List<Prop>
                    val matchResult = PropMatching().containsAllIgnoringOrder(actual, expected.toList())
                    this.matchResult = matchResult
                    return matchResult.matches
                }
            }
        }

        fun containsAtLeast(vararg expected: Prop): Matcher<List<Prop>?> {
            return object : BaseMatcher<List<Prop>>() {
                var matchResult: PropMatchResult? = null

                override fun describeTo(description: Description?) {
                    describe(description, expected, matchResult)
                }

                override fun matches(actualValue: Any?): Boolean {
                    @Suppress("UNCHECKED_CAST")
                    val actual = actualValue as? List<Prop>
                    val matchResult = PropMatching().containsAtLeast(actual, expected.toList())
                    this.matchResult = matchResult
                    return matchResult.matches
                }
            }
        }

        fun isEmpty(): Matcher<List<Prop>?> {
            return contains()
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
