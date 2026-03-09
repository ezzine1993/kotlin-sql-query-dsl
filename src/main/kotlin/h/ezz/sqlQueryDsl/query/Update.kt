package h.ezz.sqlQueryDsl.query

import h.ezz.sqlQueryDsl.components.Arguments
import h.ezz.sqlQueryDsl.components.Clause
import h.ezz.sqlQueryDsl.components.DMLStatement

/**
 * Represents the `UPDATE` statement in SQL, used for modifying existing rows in a table.
 *
 * This class extends `DMLStatement` and provides functionality specific to building
 * an `UPDATE` query, such as setting values for columns and defining additional clauses.
 *
 * Key features:
 * - Uses the `SET` clause to specify updated column values.
 * - Allows specifying a `WHERE` clause to filter the rows to update.
 * - Supports defining a `RETURNING` clause to fetch updated values after execution.
 *
 * Functions:
 * - `set(block: Arguments.() -> Unit)`: Configures the `SET` clause by specifying
 *   the columns and values to update using a lambda block.
 * - `build()`: Constructs and returns the complete SQL `UPDATE` statement as a string.
 *
 * Inherits common functionality from the `DMLStatement` class, such as setting the target
 * table and adding `WHERE` and `RETURNING` clauses.
 */
class Update : DMLStatement("UPDATE", ", ") {
    private val setClause: Clause = Clause("SET", separator = ", ")


    /**
     * Configures the `SET` clause by applying the specified configuration block to set the values
     * of the columns to be updated in an SQL `UPDATE` statement.
     *
     * @param block A lambda expression that operates on an [Arguments] instance to define the columns
     * and their corresponding values for the `SET` clause.
     */
    fun set(block: Arguments.() -> Unit) {
        setClause.applyArguments(block)
    }


    /**
     * Builds and returns the SQL query as a string.
     *
     * The method constructs the SQL query by combining various components such as
     * the table name, `SET` clause, `WHERE` clause, and `RETURNING` clause into a single cohesive string.
     * Each component is represented as an object and its string representation is appended
     * to the query in the appropriate sequence.
     *
     * @return The complete SQL query as a string.
     */
    override fun build(): String =
        StringBuilder()
            .apply {
                append(name)
                append(" ")
                table?.also { append(it.build()) }
                append(lineSeparator)
                append(setClause.build())
                where?.also {
                    append(lineSeparator)
                    append(it.build())
                }
                returning?.also {
                    append(lineSeparator)
                    append(it.build())
                }

            }.toString()
}