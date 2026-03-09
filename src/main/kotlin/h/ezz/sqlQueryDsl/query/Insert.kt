package h.ezz.sqlQueryDsl.query

import h.ezz.sqlQueryDsl.components.Arguments
import h.ezz.sqlQueryDsl.components.DMLStatement
import h.ezz.sqlQueryDsl.components.Expression
import h.ezz.sqlQueryDsl.components.function
import h.ezz.sqlQueryDsl.components.text
import h.ezz.sqlQueryDsl.components.value
import h.ezz.sqlQueryDsl.components.wrapParentheses

/**
 * Represents an SQL `INSERT` statement.
 *
 * The `Insert` class facilitates the creation of dynamic and composable SQL `INSERT` queries.
 * It allows specifying target columns, values to be inserted, and the use of a `SELECT` statement
 * as an alternative data source.
 *
 * This class inherits from `DMLStatement`, which provides common functionality for data manipulation
 * queries like `INSERT`, `UPDATE`, and `DELETE`.
 */
class Insert() : DMLStatement("INSERT INTO", ", ") {
    private var values: Expression? = null
    private var select: Select? = null


    /**
     * Specifies the columns for an SQL `INSERT` statement.
     *
     * This method allows defining the list of columns into which the values will be inserted.
     *
     * @param columns A variable number of column names as strings to be included in the `INSERT` statement.
     */
    fun columns(vararg columns: String) {
        applyArguments { columns.forEach { value(it) } }
    }

    /**
     * Specifies the values for an SQL `INSERT` statement.
     *
     * This method allows defining the list of values to be inserted into the columns of
     * a table. Each value can either be a raw value or a string literal. String literal
     * values are wrapped in single quotes, whereas non-string values are treated as raw
     * values in the SQL expression.
     *
     * @param values A variable number of values to be included in the `INSERT` statement.
     * Each value can be of any type, with strings being wrapped as literals.
     */
    fun values(vararg values: Any) {
        val arguments = Arguments(", ")
            .apply {
                values.forEach { if (it is String) text(it) else value(it) }
            }

        this.values = function("VALUES ", arguments)
    }

    /**
     * Configures and applies the given block to a `Select` instance and associates it with the `Insert` statement.
     *
     * This method enables the construction of a `Select` statement to define the selection of data used in conjunction
     * with an `Insert` statement. The `block` parameter allows a scoped configuration of the `Select` instance.
     *
     * @param block A lambda function with a receiver of type `Select` used to configure the select statement.
     * @return The configured instance of `Select`.
     */
    fun select(block: Select.() -> Unit): Select {
        val select = Select().apply(block)
        this.select = select
        return select
    }


    /**
     * Constructs and returns the SQL query string for the current `Insert` statement by combining
     * its components such as table, columns, values, select, and returning clause.
     *
     * This method ensures all elements of the `Insert` query are properly concatenated in the correct
     * logical order with appropriate separators, making the resulting string a valid SQL query.
     *
     * @return The constructed SQL `Insert` query string.
     */
    override fun build(): String {
        return StringBuilder()
            .apply {
                append(name)
                append(" ")
                table?.let { append(it.build()) }
                if (arguments.arguments.isNotEmpty()) {
                    append(" ")
                    append(wrapParentheses(arguments).build())
                }
                values?.also {
                    append(lineSeparator)
                    append(it.build())
                }


                select?.also {
                    append(lineSeparator)
                    append(it.build())
                }
                returning?.also {
                    append(lineSeparator)
                    append(it.build())
                }

            }.toString()
    }
}