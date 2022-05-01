package com.neovibrant.kacey.matchers

import org.hamcrest.Description

internal object Descriptioner {

    internal fun describe(description: Description?, expected: Any?, matchResult: PropMatchResult?) {
        if (matchResult != null) {
            description
                ?.appendText("to match expectation, but instead:\n\n***** Failure detail *****\n")
            if (matchResult.noExtraOptionsFailure) {
                describeNoExtraOptions(description, matchResult)
            } else if (matchResult.arraySizeMismatch) {
                describeArraySizeMismatch(description, matchResult)
            } else if (matchResult.wrongOrder) {
                describeWrongOrder(description, matchResult)
            } else {
                describeFailure(description, matchResult)
            }
            description
                ?.appendText("***** End of detail *****\n")
        }
        description
            ?.appendText("\nFull expectation: ")
            ?.appendValue(expected)
    }

    private fun describeNoExtraOptions(
        description: Description?,
        matchResult: PropMatchResult
    ) {
        description
            ?.appendText("\tMatching failed due to EXTRA PROP(s) at path: ")
            ?.appendValue(matchResult.options.path.takeIf { it.isNotBlank() }
                ?: "(root of object)")
            ?.appendText("\n\tExtra properties: ")
            ?.appendValue(matchResult.actual)
            ?.appendText("\n")
    }

    private fun describeArraySizeMismatch(
        description: Description?,
        matchResult: PropMatchResult
    ) {
        description
            ?.appendText("\tMatching failed due to mis-matching array SIZE at path: ")
            ?.appendValue(matchResult.options.path.takeIf { it.isNotBlank() }
                ?: "(root of object)")
            ?.appendText("\n\tExpected size: ")
            ?.appendValue(matchResult.expected)
            ?.appendText("\n\tActual size: ")
            ?.appendValue(matchResult.actual)
            ?.appendText("\n")
    }

    private fun describeWrongOrder(description: Description?, matchResult: PropMatchResult) {
        description
            ?.appendText("\tMatching failed due to WRONG ORDER of values at path: ")
            ?.appendValue(matchResult.options.path.takeIf { it.isNotBlank() }
                ?: "(root of object)")
            ?.appendText("\n")
    }

    private fun describeFailure(
        description: Description?,
        matchResult: PropMatchResult
    ) {
        description
            ?.appendText("\tMatching failed for key: ")
            ?.appendValue(matchResult.options.path)
            ?.appendText("\n\tExpected value: ")
            ?.appendValue(matchResult.expected)
            ?.appendText("\n\tActual value: ")
            ?.appendValue(matchResult.actual)
            ?.appendText("\n")
    }
}
