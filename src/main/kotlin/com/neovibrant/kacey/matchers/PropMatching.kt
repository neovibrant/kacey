package com.neovibrant.kacey.matchers

import com.neovibrant.kacey.matchers.ApiJsonMatcher.NoExtraProps.noExtraProps
import com.neovibrant.kacey.matchers.ApiJsonMatcher.Nothing.nothing

internal class PropMatching {

    internal fun match(actual: Prop?, expected: Prop, options: PropMatchOptions = PropMatchOptions()): PropMatchResult {
        val (_, checkNoExtraProps) = options
        val noExtraPropsExpected =
            checkNoExtraProps || expected.keys.contains(noExtraProps.first)
        val allMatchResults = expected
            .map { (expectedKey, expectedValue) ->
                val actualValue = actual?.get(expectedKey)
                val options = options.appendingPath(expectedKey).copy(checkNoExtraProps = noExtraPropsExpected)
                val matching = matchingResultFor(actualValue, expectedValue, options)
                val matches = when (expectedValue) {
                    is ApiJsonMatcher.Something -> matching(actualValue != null)
                    is ApiJsonMatcher.Nothing -> matching(actualValue == null)
                    noExtraProps.second -> matching(true)
                    is Map<*, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        val actualProp = (actualValue as? Prop) ?: mapOf()

                        @Suppress("UNCHECKED_CAST")
                        val expectedProp = expectedValue as Prop
                        match(actualProp, expectedProp, options)
                    }
                    is List<*> -> {
                        val listOfProps = expectedValue.all { it is Map<*, *> }
                        if (listOfProps) {
                            @Suppress("UNCHECKED_CAST")
                            val expectedValueList = expectedValue as List<Prop>

                            @Suppress("UNCHECKED_CAST")
                            val actualValueList = actualValue as? List<Prop>
                            if (actualValueList == null) {
                                matching(actualValue != null)
                            } else {
                                containsAllInOrder(
                                    actual = actualValueList,
                                    expected = expectedValueList,
                                    options = options
                                )
                            }
                        } else {
                            matching(actualValue == expectedValue)
                        }
                    }
                    else -> matching(actualValue == expectedValue)
                }
                matches
            }

        return allMatchResults
            .firstOrNull { !it.matches }
            ?: if (noExtraPropsExpected) {
                val matches = (actual?.keys?.size ?: 0) == expected.keys
                    .asSequence()
                    .filter { it != noExtraProps.first }
                    .filter { expected[it] != nothing }
                    .count()
                PropMatchResult(
                    actual = actual?.keys?.minus(expected.keys),
                    expected = null,
                    options = options,
                    matches = matches,
                    noExtraOptionsFailure = !matches
                )
            } else {
                matchingResultFor(actual, expected, options)(true)
            }
    }

    private fun matchingResultFor(
        actual: Any?,
        expected: Any?,
        options: PropMatchOptions
    ): (Boolean) -> PropMatchResult = { matches ->
        PropMatchResult(
            actual = actual,
            expected = expected,
            options = options,
            matches = matches
        )
    }

    internal fun containsAllInOrder(
        actual: List<Prop>?,
        expected: List<Prop>,
        options: PropMatchOptions = PropMatchOptions()
    ): PropMatchResult {
        val actualSize = actual?.size ?: 0
        val expectedSize = expected.size
        val expectedAndActualHaveSameSize = actualSize == expectedSize
        val allMatchInOrderResults = {
            actual
                ?.mapIndexed { index, actualProp ->
                    val expectedProp = expected[index]
                    match(actualProp, expectedProp, options.appendingPath("[$index]"))
                }
                ?: emptyList()
        }

        return if (expectedAndActualHaveSameSize) {
            allMatchInOrderResults()
                .firstOrNull { !it.matches }
                ?: matchingResultFor(actual, expected, options)(true)
        } else {
            PropMatchResult(
                actual = actualSize,
                expected = expectedSize,
                options = options,
                matches = false,
                arraySizeMismatch = true
            )
        }
    }
}


internal data class PropMatchResult(
    val actual: Any?,
    val expected: Any?,
    val options: PropMatchOptions,
    val matches: Boolean,
    val noExtraOptionsFailure: Boolean = false,
    val arraySizeMismatch: Boolean = false
)

internal data class PropMatchOptions(
    val paths: List<String> = emptyList(),
    val checkNoExtraProps: Boolean = false
) {
    fun appendingPath(path: String): PropMatchOptions {
        return this.copy(paths = this.paths.plus(path))
    }

    val path: String
        get(): String {
            return paths.joinToString(".") {
                if (it.contains("\\s".toRegex())) {
                    "'$it'"
                } else {
                    it
                }
            }
        }
}
