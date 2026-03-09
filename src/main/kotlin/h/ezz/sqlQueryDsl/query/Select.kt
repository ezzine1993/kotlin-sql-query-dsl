package h.ezz.sqlQueryDsl.query

import h.ezz.sqlQueryDsl.components.Arguments
import h.ezz.sqlQueryDsl.components.Clause
import h.ezz.sqlQueryDsl.components.Expression
import h.ezz.sqlQueryDsl.components.SQLiteral
import h.ezz.sqlQueryDsl.components.Statement
import h.ezz.sqlQueryDsl.components.Wrapper
import h.ezz.sqlQueryDsl.components.toLiteral
import h.ezz.sqlQueryDsl.components.value

/**
 * Represents a SQL SELECT statement for building and executing structured queries.
 *
 * This class extends the `Statement` class, providing specific functionality to configure SQL queries
 * with features like projection, grouping, ordering, limits, offsets, and combining queries.
 *
 * The `Select` class supports advanced query customizations, including `GROUP BY`, `HAVING`, `ORDER BY`,
 * `LIMIT`, and `OFFSET` clauses. Additionally, it allows combining statements with operators such as
 * `UNION`, `UNION ALL`, `INTERSECT`, and `EXCEPT`.
 *
 * Constructors:
 * - `Select()`: Initializes an empty SELECT statement.
 *
 * Functions:
 * - `UNION(to: Select)`: Combines the current `SELECT` statement with another `SELECT` statement
 *   using the `UNION` operator.
 * - `UNIONALL(to: Select)`: Combines the current `SELECT` statement with another `SELECT` statement
 *   using the `UNION ALL` operator.
 * - `INTERSECT(to: Select)`: Combines the current `SELECT` statement with another `SELECT` statement
 *   using the `INTERSECT` operator.
 * - `EXCEPT(to: Select)`: Combines the current `SELECT` statement with another `SELECT` statement
 *   using the `EXCEPT` operator.
 * - `limit(l: Int)`: Adds a `LIMIT` clause with the provided numeric value to limit the number of rows in the result set.
 * - `offset(o: Int)`: Adds an `OFFSET` clause with the provided numeric value to specify the starting point of rows in the result set.
 * - `projections(block: Arguments.() -> Unit)`: Configures the expressions or columns to be projected in the SELECT clause.
 * - `having(block: Arguments.() -> Unit)`: Configures the `HAVING` clause to filter grouped data based on specified conditions.
 * - `groupBy(block: Arguments.() -> Unit)`: Configures the `GROUP BY` clause to group rows based on specified columns or expressions.
 * - `orderBy(block: OrderBy.() -> Unit)`: Configures the `ORDER BY` clause to specify the sort order of rows in the result set.
 * - `build()`: Builds and constructs the complete SQL query including all configured clauses.
 *
 * Nested Classes:
 * - `OrderBy`: Represents the `ORDER BY` clause and provides methods to define sorting, such as `asc` and `desc` for ordering columns.
 */
class Select() : Statement("SELECT", separator = ", ") {
    private var groupBy: Clause? = null
    private var having: Clause? = null
    private var orderBy: Clause? = null
    private var limit: Clause? = null
    private var offset: Clause? = null
    private var secondarySelects: Clause? = null


    infix fun UNION(to: Select): Select = setSecondarySelect(to, "UNION")
    infix fun UNIONALL(to: Select): Select = setSecondarySelect(to, "UNION ALL")
    infix fun INTERSECT(to: Select): Select = setSecondarySelect(to, "INTERSECT")
    infix fun EXCEPT(to: Select): Select = setSecondarySelect(to, "EXCEPT")

    /**
     * Updates the current `Select` instance by appending a secondary `Select` statement
     * with a specified SQL set operator (e.g., UNION, INTERSECT, etc.).
     *
     * @param select The secondary `Select` statement to append.
     * @param operator The SQL set operator to use (e.g., "UNION", "INTERSECT").
     * @return The current `Select` instance with the appended secondary `Select` and operator.
     */
    private fun setSecondarySelect(select: Select, operator: String): Select {
        val union = Clause(operator, "")
        union.applyArguments {
            this@Select.secondarySelects?.also { updateValue(it) }
            updateValue(select)
        }

        secondarySelects = union

        return this
    }

    /**
     * Sets a `LIMIT` clause for the current SQL `Select` statement.
     *
     * The `LIMIT` clause specifies the maximum number of rows to be returned
     * in the result set of the query.
     *
     * @param l The maximum number of rows to be returned. Must be a non-negative integer.
     */
    fun limit(l: Int) {
        limit = Clause(name = "LIMIT", separator = "")
        limit?.applyArguments { value(l) }
    }

    /**
     * Sets an `OFFSET` clause for the current SQL `Select` statement.
     *
     * The `OFFSET` clause specifies the number of rows to skip in the result set
     * of the query before starting to return rows.
     *
     * @param o The number of rows to skip. Must be a non-negative integer.
     */
    fun offset(o: Int) {
        offset = Clause(name = "OFFSET", separator = "")
        offset?.applyArguments { value(o) }
    }

    /**
     * Defines the selection of columns or computed projections in a SQL `SELECT` statement.
     *
     * This function accepts a block of code that operates on an [Arguments] instance,
     * allowing you to specify one or more projections (e.g., columns or computed expressions)
     * to be included in the query's result set.
     *
     * @param block A lambda that configures the projections by operating on an [Arguments] instance.
     */
    fun projections(block: Arguments.() -> Unit) {
        applyArguments(block)
    }

    /**
     * Configures a `HAVING` clause for the current SQL `Select` statement.
     *
     * The `HAVING` clause is used to filter the grouped results of a `GROUP BY` clause
     * based on the specified condition(s). This method accepts a lambda that operates
     * on an [Arguments] instance, allowing you to define the conditions for the clause.
     *
     * @param block A lambda function that configures the `HAVING` clause by specifying
     * conditions using the provided [Arguments] instance.
     */
    fun having(block: Arguments.() -> Unit) {
        having = Clause("HAVING", " ")
        having?.applyArguments(block)
    }

    /**
     * Configures a `GROUP BY` clause for the current SQL `Select` statement.
     *
     * The `GROUP BY` clause is used to group rows in a query result set into summary rows
     * based on one or more columns or computed expressions.
     * This method accepts a lambda that operates on an [Arguments] instance, allowing
     * you to specify the grouping criteria for the clause.
     *
     * @param block A lambda function that configures the `GROUP BY` clause by specifying
     * grouping criteria using the provided [Arguments] instance.
     */
    fun groupBy(block: Arguments.() -> Unit) {
        groupBy = Clause("GROUP BY", ", ")
        groupBy?.applyArguments(block)
    }

    /**
     * Configures an `ORDER BY` clause for the current SQL `Select` statement.
     *
     * The `ORDER BY` clause is used to sort the rows in the result set of the query
     * based on one or more columns or expressions, either in ascending or descending order.
     * This function accepts a lambda that operates on an [OrderBy] instance, allowing you
     * to define the sorting criteria.
     *
     * @param block A lambda function that configures the `ORDER BY` clause by specifying
     * sorting criteria using the provided [OrderBy] instance.
     */
    fun orderBy(block: OrderBy.() -> Unit) {
        val arguments = OrderBy().apply(block)
        orderBy = Clause("ORDER BY", ", ", arguments = arguments)


    }


    /**
     * Builds and returns the final SQL query string by combining the components
     * of the `Select` class such as `FROM`, `WHERE`, `GROUP BY`, `HAVING`,
     * `ORDER BY`, `LIMIT`, `OFFSET`, and secondary selects.
     *
     * The method ensures that each component is appended in the correct logical
     * order, separated by line breaks where necessary. This facilitates the construction
     * of a complete SQL query in string format.
     *
     * @return The constructed SQL query string.
     */
    override fun build(): String =
        StringBuilder().apply {
            fun SQLiteral.append() {
                append(build())
                append(lineSeparator)
            }

            append(super.build())
            append(" ")
            from?.append()
            where?.append()
            groupBy?.append()
            having?.append()
            orderBy?.append()
            limit?.append()
            offset?.append()
            secondarySelects?.append()

        }.toString()

    /**
     * Represents an `ORDER BY` clause configuration for SQL queries.
     *
     * The `OrderBy` class provides methods for specifying sorting criteria
     * in ascending (`ASC`) or descending (`DESC`) order for SQL expressions.
     * These methods can be used to define the sorting logic for a query's result set.
     *
     * This class operates within the context of a `Select` statement and is designed
     * to be used as part of configuring the query's sorting conditions.
     */
    class OrderBy() : Arguments() {

        fun Expression.desc(value: Any): SQLiteral =
            updateValue(Wrapper(open = "", close = " DESC", value = value.toLiteral()))

        fun Expression.asc(value: Any): SQLiteral =
            updateValue(Wrapper(open = "", close = " ASC", value = value.toLiteral()))

    }

}
