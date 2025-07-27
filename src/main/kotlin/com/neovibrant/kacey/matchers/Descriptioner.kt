package com.neovibrant.kacey.matchers

internal object Descriptioner {
    internal fun describe(
        expected: Any?,
        matchResult: PropMatchResult?,
    ): String {
        val description = StringBuilder()

        if (matchResult != null) {
            description.append("to match expectation, but instead:\n\n***** Failure detail *****\n")
            if (matchResult.noExtraOptionsFailure) {
                description.append(describeNoExtraOptions(matchResult))
            } else if (matchResult.arraySizeMismatch) {
                description.append(describeArraySizeMismatch(matchResult))
            } else if (matchResult.wrongOrder) {
                description.append(describeWrongOrder(matchResult))
            } else {
                description.append(describeFailure(matchResult))
            }
            description.append("***** End of detail *****\n")
        }
        description.append("\nFull expectation: $expected")

        return description.toString()
    }

    private fun describeNoExtraOptions(
        matchResult: PropMatchResult,
    ): String {
        val path = matchResult.options.path.takeIf { it.isNotBlank() } ?: "(root of object)"
        return "\tMatching failed due to EXTRA PROP(s) at path: \"$path\"\n\tExtra properties: ${matchResult.actual}\n"
    }

    private fun describeArraySizeMismatch(
        matchResult: PropMatchResult,
    ): String {
        val path = matchResult.options.path.takeIf { it.isNotBlank() } ?: "(root of object)"
        return "\tMatching failed due to mis-matching array SIZE at path: \"$path\"\n\tExpected size: <${matchResult.expected}>\n\tActual size: <${matchResult.actual}>\n"
    }

    private fun describeWrongOrder(
        matchResult: PropMatchResult,
    ): String {
        val path = matchResult.options.path.takeIf { it.isNotBlank() } ?: "(root of object)"
        return "\tMatching failed due to WRONG ORDER of values at path: \"$path\"\n"
    }

    private fun describeFailure(
        matchResult: PropMatchResult,
    ): String {
        return "\tMatching failed for key: \"${matchResult.options.path}\"\n\tExpected value: \"${matchResult.expected}\"\n\tActual value: \"${matchResult.actual}\"\n"
    }
}
