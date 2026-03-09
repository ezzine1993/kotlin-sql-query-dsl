package h.ezz.sqlQueryDsl

import h.ezz.sqlQueryDsl.components.SQLiteral
import h.ezz.sqlQueryDsl.components.Statement
import h.ezz.sqlQueryDsl.query.Delete
import h.ezz.sqlQueryDsl.query.Insert
import h.ezz.sqlQueryDsl.query.Select
import h.ezz.sqlQueryDsl.query.Update

/**
 * A builder class used to construct SQL queries dynamically.
 *
 * The `QueryBuilder` class provides functions to build different types of SQL statements,
 * including `SELECT`, `INSERT`, `UPDATE`, and `DELETE`. These methods accept lambda expressions
 * to customize the structure and arguments of the query.
 *
 * The constructed SQL is represented as a string through the implementation of the `build` method
 * from the `SQLiteral` interface.
 */
class QueryBuilder() : SQLiteral {
    companion object {
        /**
         * Determines whether the SQL query output should be formatted for improved readability.
         *
         * When set to `true`, the generated SQL statements will include additional formatting
         * such as line breaks and indentation, making them easier to read and debug. When set to `false`,
         * the SQL output will be compact with minimal spacing.
         *
         * This property is primarily used within the `QueryBuilder` class to influence the formatting
         * style of constructed SQL queries.
         */
        internal var pretty = true
    }

    /**
     * Holds the current SQL statement being built or managed by the `QueryBuilder`.
     *
     * This property is used to store an instance of a `Statement` subclass (e.g., `Select`, `Insert`,
     * `Update`, or `Delete`) that represents the active SQL query being constructed. Its value is
     * initialized as `null` and can be set through methods within the `QueryBuilder` that create or
     * configure SQL statements.
     *
     * Once a statement is set, it remains the active statement until explicitly overridden or cleared.
     * It is utilized internally by the `QueryBuilder` methods to ensure proper construction and
     * management of SQL queries.
     */
    private var statement: Statement? = null

    /**
     * Constructs and returns a `Select` statement for building SQL queries.
     *
     * This method initializes a new `Select` instance and applies the given configuration
     * block to customize the SQL query. If there is no existing statement, the created `Select`
     * instance is set as the current statement in the `QueryBuilder`.
     *
     * @param block The configuration block used to customize the `Select` instance.
     * @return The constructed `Select` instance after applying the configuration block.
     */
    fun select(block: Select.() -> Unit): Select {
        val select = Select().apply(block)
        if (statement == null)
            statement = select
        return select
    }

    /**
     * Constructs and returns an `Insert` statement for building SQL `INSERT` queries.
     *
     * This method initializes a new `Insert` instance and applies the given configuration
     * block to customize the SQL query. If there is no existing statement, the created `Insert`
     * instance is set as the current statement in the `QueryBuilder`.
     *
     * @param block A lambda function with a receiver of type `Insert` used to configure the `Insert` statement.
     * @return The constructed `Insert` instance after applying the configuration block.
     */
    fun insertInto(block: Insert.() -> Unit): Insert {
        return setStatement(Insert().apply(block))
    }

    /**
     * Constructs and returns an `Update` statement for building SQL `UPDATE` queries.
     *
     * This method initializes a new `Update` instance and applies the given configuration
     * block to customize the SQL query. If there is no existing statement, the created `Update`
     * instance is set as the current statement in the `QueryBuilder`.
     *
     * @param block A lambda function with a receiver of type `Update` used to configure the `Update` statement.
     * @return The constructed `Update` instance after applying the configuration block.
     */
    fun update(block: Update.() -> Unit): Update =
        setStatement(Update().apply(block))

    /**
     * Constructs and returns a `Delete` statement for building SQL `DELETE` queries.
     *
     * This method initializes a new `Delete` instance and applies the given configuration
     * block to customize the SQL query. If there is no existing statement, the created `Delete`
     * instance is set as the current statement in the `QueryBuilder`.
     *
     * @param block A lambda expression with a receiver of type `Delete` used to configure the `Delete` statement.
     * @return The constructed `Delete` instance after applying the configuration block.
     */
    fun deleteFrom(block: Delete.() -> Unit): Delete =
        setStatement(Delete().apply(block))

    /**
     * Sets the current statement in the `QueryBuilder` if it has not been initialized, and returns the provided statement.
     *
     * This function ensures that once a statement is set, it cannot be overridden unless explicitly cleared. It is used
     * to manage the active statement within the `QueryBuilder`.
     *
     * @param new The statement to set as the current statement. It must be a subtype of `Statement`.
     * @return The provided statement, which is either newly set or already existing.
     */
    private fun <T : Statement> setStatement(new: T): T {
        if (statement == null)
            statement = new
        return new
    }

    override fun build(): String = statement?.build().orEmpty()

}

/**
 * Creates and returns an instance of `QueryBuilder` while configuring it using the provided lambda expression.
 *
 * This function allows the caller to construct SQL queries dynamically by supplying a block of
 * configuration logic. The `pretty` parameter influences whether the query output is formatted
 * for better readability.
 *
 * @param pretty A Boolean flag indicating whether the resulting query should be formatted for readability. Defaults to `false`.
 * @param block A lambda expression with a receiver of type `QueryBuilder` that defines how the `QueryBuilder` should be configured.
 * @return A configured instance of `QueryBuilder`.
 */
fun queryBuilder(pretty: Boolean = false, block: QueryBuilder.() -> Unit): QueryBuilder {
    QueryBuilder.pretty = pretty
    return QueryBuilder().apply { block() }
}
