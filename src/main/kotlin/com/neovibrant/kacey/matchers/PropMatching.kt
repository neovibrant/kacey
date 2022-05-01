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
            val result = allMatchInOrderResults()
                .firstOrNull { !it.matches }
                ?: matchingResultFor(actual, expected, options)(true)
            if (!result.matches && containsAllIgnoringOrder(actual, expected, options).matches) {
                PropMatchResult(
                    actual = actual,
                    expected = expected,
                    options = options,
                    matches = false,
                    wrongOrder = true
                )
            } else {
                result
            }
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

    internal fun containsAllIgnoringOrder(
        actual: List<Prop>?,
        expected: List<Prop>,
        options: PropMatchOptions = PropMatchOptions()
    ): PropMatchResult {
        val expectedAndActualHaveSameSize = (actual?.size ?: 0) == expected.size

        return if (!expectedAndActualHaveSameSize) {
            PropMatchResult(
                actual = actual?.size ?: 0,
                expected = expected.size,
                options = options,
                matches = false,
                arraySizeMismatch = true
            )
        } else {
            val nonMatchingActual = actual
                ?.mapIndexedNotNull { index, actualProp ->
                    expected
                        .none { expectedProp ->
                            match(actualProp, expectedProp, options).matches
                        }
                        .takeIf { it }
                        ?.let {
                            PropMatchResult(
                                actual = actualProp,
                                expected = "<Nothing that matched in any order>",
                                options = options.appendingPath("[$index]"),
                                matches = false
                            )
                        }
                }
                ?.firstOrNull()
            val nonMatchingExpected = expected
                .mapIndexedNotNull { index, expectedProp ->
                    (actual
                        ?.none { actualProp ->
                            match(actualProp, expectedProp, options).matches
                        } ?: true)
                        .takeIf { it }
                        ?.let {
                            PropMatchResult(
                                actual = "<Nothing that matched in any order>",
                                expected = expectedProp,
                                options = options.appendingPath("[$index]"),
                                matches = false
                            )
                        }
                }
                .firstOrNull()

            if (nonMatchingExpected != null) {
                nonMatchingExpected
            } else if (nonMatchingActual != null) {
                nonMatchingActual
            } else {
                PropMatchResult(
                    actual = actual,
                    expected = expected,
                    options = options,
                    matches = true
                )
            }
        }
    }
}


internal data class PropMatchResult(
    val actual: Any?,
    val expected: Any?,
    val options: PropMatchOptions,
    val matches: Boolean,
    val noExtraOptionsFailure: Boolean = false,
    val arraySizeMismatch: Boolean = false,
    val wrongOrder: Boolean = false
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
