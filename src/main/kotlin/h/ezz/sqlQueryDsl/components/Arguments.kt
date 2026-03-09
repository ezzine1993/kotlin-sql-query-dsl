package h.ezz.sqlQueryDsl.components



/**
 * Represents a collection of SQL arguments combined with a specific separator.
 *
 * This class serves as a container for SQLLiteral elements, allowing them to be combined into
 * a single SQL expression string. The arguments are joined using the specified separator.
 *
 * @constructor Creates an instance of [Arguments] with an optional separator and a list of initial arguments.
 * @property separator The string used to separate arguments when building the SQL expression.
 * Default is a single space.
 * @param list A list of initial arguments to populate the [arguments] collection.
 * Each argument in the list is converted to an instance of [SQLiteral].
 *
 */
@SQLQueryMaker
open class Arguments(val separator: String = " ", list: List<Any> = emptyList()) : Expression() {
    val arguments = mutableListOf<SQLiteral>().apply { addAll(list.map { it.toLiteral() }) }


    /**
     * Updates the value of the current instance by either replacing or appending the given SQLiteral,
     * while ensuring the proper management of existing arguments. If the new value is a wrapper
     * around the last added argument, the wrapper replaces the last argument in the collection.
     *
     * @param new The new SQLiteral to be added or used to update the existing collection of arguments.
     * @return The newly added or updated SQLiteral.
     */
    override fun updateValue(new: SQLiteral): SQLiteral {
        (new as? Expression)?.value()?.also {
            arguments.remove(it)
        }
        arguments.add(new)
        return new
    }

    /**
     * Builds a composite SQL expression string by joining all the contained arguments with the
     * defined separator.
     *
     * @return A string representing the constructed SQL expression, with each argument's
     * build representation concatenated using the specified separator.
     */
    override fun build(): String = arguments.joinToString(separator) { it.build() }
}