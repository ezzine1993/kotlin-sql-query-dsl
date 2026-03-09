package h.ezz.sqlQueryDsl.components

/**
 * Represents a base interface for building SQL literals.
 *
 * This interface provides the foundational structure for SQL components that can be represented
 * as literal strings. It enforces a consistent way to define and construct SQL parts.
 *
 * Classes implementing this interface should provide their own implementation of the [build] method
 * to generate a string representation of their SQL structure.
 *
 * @property name The name of the SQL literal. Defaults to an empty string.
 */
interface SQLiteral {
    val name: String
        get() = ""

    fun build(): String
}

/**
 * Represents a SQL literal value.
 *
 * This class encapsulates a single value that can be used as a literal within an SQL query.
 * It implements the [SQLiteral] interface, which ensures that the value can be converted
 * into a string representation suitable for inclusion in SQL statements.
 *
 * @property value The underlying value of the literal. It can be of any type, including null.
 */
class Value(val value: Any?) : SQLiteral {
    override fun build(): String = "$value"
}

/**
 * Converts the current object to an instance of [SQLiteral].
 *
 * If the receiver is already an instance of [SQLiteral], it is returned as is.
 * Otherwise, the receiver is wrapped in a [Value] object.
 *
 * @return An instance of [SQLiteral], either as the original object or wrapped in a [Value].
 */
fun Any?.toLiteral(): SQLiteral = when (this) {
    is SQLiteral -> this
    else -> Value(this)
}