package h.ezz.sqlQueryDsl.components

import h.ezz.sqlQueryDsl.QueryBuilder.Companion.pretty



/**
 * Represents a generic SQL clause used for building structured SQL queries.
 *
 * A clause consists of a name and optional arguments that define its content.
 * The `Clause` class allows combining SQL literals and arguments, represented by the `Arguments` class,
 * into a cohesive clause string. The clause is constructed using the `build` method to generate the resulting SQL statement.
 *
 * The class provides a mechanism for managing the clause's arguments via the `applyArguments` function,
 * which accepts a configuration block to modify the internal state of the `Arguments` instance.
 *
 * Properties:
 * - `name`: The name of the clause (e.g., `SELECT`, `WHERE`, `JOIN`).
 * - `separator`: A string used to separate arguments within the clause. Defaults to an empty string.
 * - `arguments`: Manages the internal list of SQL arguments associated with the clause, represented as an `Arguments` object.
 * - `lineSeparator`: Determines the line break style for the clause when generating its SQL representation, influenced by formatting preferences.
 *
 * Functions:
 * - `applyArguments`: Configures the `Arguments` instance by applying the provided block to its internal state.
 * - `build`: Constructs and returns the string representation of the SQL clause, including its name and assembled arguments.
 */
@SQLQueryMaker
open class Clause(
    override val name: String,
    val separator: String = "",
    protected val arguments: Arguments = Arguments(separator)
) : SQLiteral {
    protected val lineSeparator = if (pretty) "\n" else " "

    /**
     * Applies the provided configuration block to the `Arguments` instance.
     *
     * This function allows modification of the internal state of the `Arguments` object
     * by executing the supplied block, which operates on the `Arguments` instance.
     *
     * @param block A lambda function to be applied to the `Arguments` instance, enabling
     * customization or modification of its state.
     */
    fun applyArguments(block: Arguments.() -> Unit) {
        arguments.apply(block)
    }

    override fun build(): String = "$name ${arguments.build()}"

}
