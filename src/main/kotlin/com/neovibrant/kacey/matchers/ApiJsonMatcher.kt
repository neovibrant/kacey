package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.NoExtraProps.noExtraProps
import com.neovibrant.kacey.matchers.Descriptioner.describe
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult

typealias Prop = Map<String, Any?>

val Prop.id: String
    get() = this["id"] as String

class ApiJsonMatcher {
    val fields: MutableMap<String, Any?> = mutableMapOf()

    @Suppress("ktlint:standard:function-naming")
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
            return Matcher { actual ->
                val matchResult = PropMatching().match(actual, expected)
                MatcherResult(
                    matchResult.matches,
                    { describe(expected, matchResult) },
                    { describe(expected, matchResult) }
                )
            }
        }

        fun contains(vararg expected: Prop): Matcher<List<Prop>?> {
            return Matcher { actual ->
                val matchResult = PropMatching().containsAllInOrder(actual, expected.toList())
                MatcherResult(
                    matchResult.matches,
                    { describe(expected, matchResult) },
                    { describe(expected, matchResult) }
                )
            }
        }

        fun containsInAnyOrder(vararg expected: Prop): Matcher<List<Prop>?> {
            return Matcher { actual ->
                val matchResult = PropMatching().containsAllIgnoringOrder(actual, expected.toList())
                MatcherResult(
                    matchResult.matches,
                    { describe(expected, matchResult) },
                    { describe(expected, matchResult) }
                )
            }
        }

        fun containsAtLeast(vararg expected: Prop): Matcher<List<Prop>?> {
            return Matcher { actual ->
                val matchResult = PropMatching().containsAtLeast(actual, expected.toList())
                MatcherResult(
                    matchResult.matches,
                    { describe(expected, matchResult) },
                    { describe(expected, matchResult) }
                )
            }
        }

        fun isEmpty(): Matcher<List<Prop>?> = contains()
    }

    object NoExtraProps {
        val noExtraProps = Pair("__NO_EXTRA_PROPS__", "__NO_EXTRA_PROPS__")
    }

    object Something {
        val something = Something

        override fun toString(): String = "something"
    }

    object Nothing {
        val nothing = Nothing

        override fun toString(): String = "nothing"
    }
}
