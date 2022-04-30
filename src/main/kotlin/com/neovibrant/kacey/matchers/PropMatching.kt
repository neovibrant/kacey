package com.neovibrant.kacey.matchers

internal class PropMatching {

    internal fun match(actual: Prop?, expected: Prop, options: PropMatchOptions = PropMatchOptions()): PropMatchResult {
        val (_, checkNoExtraProps) = options
        val noExtraPropsExpected =
            checkNoExtraProps || expected.keys.contains(ApiJsonMatcher.NoExtraProps.noExtraProps.first)
        val allMatchResults = expected
            .map { (expectedKey, expectedValue) ->
                val actualValue = actual?.get(expectedKey)
                val options = options.appendingPath(expectedKey)
                val matching = matchingResultFor(actualValue, expectedValue, options)
                val matches = when (expectedValue) {
                    is ApiJsonMatcher.Something -> matching(actualValue != null)
                    is ApiJsonMatcher.Nothing -> matching(actualValue == null)
                    ApiJsonMatcher.NoExtraProps.noExtraProps.second -> matching(true)
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
                            val actualValueList = actualValue as? List<*>
                            // TODO calling the matcher directly doesn't give us enough granularity for the index failing
                            matching(
                                ApiJsonMatcher.contains(*expectedValueList.toTypedArray())
                                    .matches(actualValueList)
                            )
                        } else {
                            matching(actualValue == expectedValue)
                        }
                    }
                    else -> matching(actualValue == expectedValue)
                }
                matches
            }

        val firstMatchFailure = allMatchResults.firstOrNull { !it.matches }

        return if (firstMatchFailure != null) {
            firstMatchFailure
        } else if (noExtraPropsExpected) {
            matchingResultFor(actual, expected, options.appendingPath("<EXTRA_PROPS>"))(
                (actual?.keys?.size ?: 0) == expected.keys
                .asSequence()
                .filter { it != ApiJsonMatcher.NoExtraProps.noExtraProps.first }
                .filter { expected[it] != ApiJsonMatcher.Nothing.nothing }
                .count()
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
}


internal data class PropMatchResult(val actual: Any?, val expected: Any?, val options: PropMatchOptions, val matches: Boolean)

internal data class PropMatchOptions(val paths: List<String> = emptyList(), val checkNoExtraProps: Boolean = false) {
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
