package h.ezz.sqlQueryDsl.components

/**
 * Represents a SQL statement, such as SELECT, UPDATE, or INSERT, which is considered a specialized
 * type of clause and can serve as the main clause in SQL query generation.
 *
 * This class extends the `Clause` class and provides methods to build and configure the
 * `FROM` and `WHERE` clauses of a SQL query.
 *
 * @constructor Creates a `Statement` with the specified name, separator, and optional arguments.
 * @param name The name of the SQL statement (e.g., SELECT, UPDATE).
 * @param separator The delimiter used to separate the elements of the SQL clause.
 * @param arguments The optional arguments for the statement, initialized by default.
 */
open class Statement(
    name: String,
    separator: String,
    arguments: Arguments = Arguments(separator)
) : Clause(name, separator, arguments) {

    protected var from: Clause? = null

    protected var where: Clause? = null

    /**
     * Initializes the `from` clause of the SQL query with the specified configuration.
     *
     * @param block a lambda with receiver used to configure the `From` instance, representing
     * the structure of the `FROM` clause in a SQL query.
     */
    fun from(block: From.() -> Unit) {
        val arguments = From().apply(block)
        from = Clause("FROM", ", ", arguments = arguments)
    }


    /**
     * Configures the `WHERE` clause of the SQL query with the specified arguments.
     *
     * @param block a lambda with receiver used to configure the `Arguments` instance,
     * representing the conditions of the `WHERE` clause in a SQL query.
     */
    fun where(block: Arguments.() -> Unit) {
        where = Clause("WHERE", " ")
        where?.applyArguments(block)
    }


    /**
     * Represents the `FROM` clause in an SQL query, providing functionality to define
     * table sources and join operations for query-building.
     *
     * The `From` class extends the `Arguments` class to manage and construct the components
     * of the `FROM` clause. It defines various join methods, allowing specification of
     * join types and related conditions.
     */
    class From() : Arguments() {

        /**
         * Represents a SQL JOIN clause in a query.
         *
         * The class defines the structure and behavior of JOIN clauses, allowing construction of SQL queries
         * with `JOIN`, `ON`, and `USING` operators. It relies on the provided join type and allows
         * dynamic expressions for defining join conditions.
         *
         * @constructor Creates an instance of the `Join` class with the specified join type.
         * @param type The type of JOIN (e.g., `JOIN`, `LEFT_JOIN`).
         */
        class Join(val type: Type) : Expression() {
            enum class Type(val string: String) { JOIN("JOIN"), LEFT_JOIN("LEFT JOIN") }

            infix fun Any.ON(right: Any): SQLiteral =
                setOperator(name = "ON", right = right)

            infix fun Any.USING(column: String): SQLiteral = this.USING(listOf(column))



            infix fun Any.USING(columns: List<String>): SQLiteral =
                this.setOperator(
                    name = "USING",
                    Wrapper(
                        open = "(",
                        close = ")",
                        value = Arguments(separator = ", ", list = columns)
                    )
                )

            /**
             * Constructs and returns a string representation of the JOIN clause, including its type and any associated value.
             *
             * The string format combines the join type (e.g., "JOIN", "LEFT JOIN") with the optional
             * built value of the associated condition or clause, if available.
             *
             * @return A string representing the JOIN clause, including its type and built value.
             */
            override fun build(): String = "${type.string} ${value?.build()}"

        }


        fun From.join(type: Join.Type, block: Join.() -> Unit): Expression {
            arguments.add(Join(type).apply(block))
            return this
        }

        /**
         * Adds a join clause to the current query with a default `JOIN` type.
         *
         * @param block A lambda expression to define the join clause by applying operations on the `Join` receiver.
         * @return The updated expression representing the SQL query with the added join clause.
         */
        fun From.join(block: Join.() -> Unit): Expression =
            join(Join.Type.JOIN, block)


        fun From.leftJoin(block: Join.() -> Unit): Expression =
            join(Join.Type.LEFT_JOIN, block)

    }

}


/**
 * Represents a Data Manipulation Language (DML) SQL statement.
 *
 * This class provides a foundation for building DML queries, such as INSERT, UPDATE, and DELETE.
 * It extends the `Statement` class to inherit common functionality for SQL clauses and arguments.
 *
 * @param name The name of the DML operation (e.g., INSERT, UPDATE).
 * @param separator The string used to separate components within the statement.
 * @param arguments A set of arguments associated with the DML statement, with a default separator.
 */
open class DMLStatement(
    name: String,
    separator: String,
    arguments: Arguments = Arguments(separator)
) : Statement(name, separator, arguments) {

    /**
     * Represents the table associated with the current DML statement.
     *
     * This variable holds a reference to the SQL literal value representing the table
     * targeted by the DML operation. It can be set using the [table] function and may be null
     * if no table has been specified for the statement.
     */
    protected var table: Value? = null

    /**
     * Specifies the `RETURNING` clause of the DML statement.
     *
     * This variable holds a reference to the `Clause` representing the SQL `RETURNING` clause,
     * used to define which columns or expressions should be returned from a data manipulation operation.
     * It is null by default and can be defined using the `returning` function.
     */
    protected var returning: Clause? = null


    /**
     * Sets the table name for the current DML statement.
     *
     * This function assigns a table to the DML operation by creating a `Value`
     * instance for the given table name. The table is then associated with the
     * DML statement to specify the target of the operation, such as for an
     * `INSERT`, `UPDATE`, or `DELETE` query.
     *
     * @param name The name of the table to target in the SQL statement.
     */
    fun table(name: String) {
        table = Value(name)
    }


    fun returning(vararg columns: String) {
        returning = Clause(name = "RETURNING", ", ")
        returning?.applyArguments {
            columns.forEach { value(it) }
        }
    }

}