@file:Suppress("FunctionName")

package co.touchlab.kmp.testing.framework.dsl

abstract class ContractsDsl {

    protected fun Given(action: Given.() -> Unit): GivenBuilder {
        action(Given)

        return GivenBuilder()
    }

    protected fun When(action: When.() -> Unit): WhenBuilder {
        action(When)

        return WhenBuilder()
    }

    protected fun Then(action: Then.() -> Unit): ThenBuilder {
        action(Then)

        return ThenBuilder()
    }

    protected class GivenBuilder {

        infix fun And(action: Given.() -> Unit): GivenBuilder {
            action(Given)

            return this
        }
    }

    protected class WhenBuilder {

        infix fun And(action: When.() -> Unit): WhenBuilder {
            action(When)

            return this
        }
    }

    protected class ThenBuilder {

        infix fun And(action: Then.() -> Unit): ThenBuilder {
            action(Then)

            return this
        }
    }
}
