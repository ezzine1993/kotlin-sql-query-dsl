package h.ezz.sqlQueryDsl.components


/**
 * Represents a SQL `CASE` expression for building dynamic SQL queries.
 *
 * This class supports the creation of conditional SQL expressions using `WHEN` and `ELSE` clauses.
 * It inherits from the `Arguments` class and combines multiple SQL conditions to form a `CASE` statement
 * that concludes with `END`.
 *
 * Functions available within the `Case` class allow for defining the `WHEN` conditions
 * and specifying the `ELSE` fallback value.
 */
@SQLQueryMaker
class Case() : Arguments(separator = " ") {

    fun WHEN(block: When.() -> Unit) {
        updateValue(When().apply(block))
    }

    fun ELSE(value: Any) {
        updateValue(Wrapper("ELSE", value = value.toLiteral()))
    }

    /**
     * Represents a `WHEN` clause within a SQL `CASE` expression.
     *
     * The `When` class is used to define conditional branches of a SQL `CASE` statement.
     * It builds a SQL fragment that begins with the `WHEN` keyword, followed by a condition
     * and its corresponding result through the `THEN` operator.
     *
     * @constructor Creates an instance of the `When` class to construct a `WHEN` clause.
     */
    class When() : Expression() {
        infix fun Any.THEN(right: Any): SQLiteral = setOperator(name = "THEN", right = right)

        override fun build(): String {
            return "WHEN ${value?.build()}"
        }
    }

    override fun build(): String {
        return "${super.build()} END"
    }
}