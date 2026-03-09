/**
 * Provides the SQL DSL helper functions and utility wrappers used to build
 * SQL expressions in a fluent Kotlin style.
 *
 * This file is responsible for the "function layer" of the DSL.
 * It contains:
 *
 * - SQL type enums such as [SQLType] and [SQLAffinity]
 * - reusable wrapper builders like [wrapText] and [wrapParentheses]
 * - generic SQL function builders through [function]
 * - expression extension helpers like [value], [text], [list], and [wrap]
 * - predefined SQL functions such as [count], [sum], [avg], [replace], [coalesce], [date], and [cast]
 * - special DSL helpers for constructs like [case], [iIf], and aggregate functions
 *
 * In practice, this file is the main place where raw values and expressions
 * are transformed into SQL function calls or wrapped SQL fragments.
 *
 * Example:
 * ```
 * projections {
 *     count("*")
 *     replace(string = "name", from = "x", to = "y")
 *     coalesce("nickname", "unknown")
 * }
 * ```
 *
 * The helpers declared here do not define the full query structure
 * such as `SELECT`, `FROM`, or `WHERE`.
 * Instead, they provide the expression-building tools that are used
 * inside those clauses.
 *
 * Typical responsibilities of this file:
 *
 * - convert Kotlin values into SQL literals
 * - wrap values in quotes or parentheses
 * - build SQL function calls
 * - expose common SQL built-in functions as DSL extensions
 * - support expression composition inside projections, conditions, and other clauses
 *
 * This file should remain focused on expression utilities and SQL functions.
 * Statement-specific constructs such as query roots, clauses, joins, or statement
 * types should live in their own files.
 */
package h.ezz.sqlQueryDsl.components

/**
 * Represents SQLite storage types used when casting values.
 */
enum class SQLType { NULL, INTEGER, REAL, TEXT, BLOB }

/**
 * Represents SQLite column affinity types.
 */
enum class SQLAffinity { INTEGER, TEXT, REAL, NUMERIC, BLOB }

/**
 * SQL wildcard representing all columns (`*`).
 *
 * Example:
 * ```
 * projections { all() }
 * ```
 */
val all
    get() = Value("*")

/**
 * Wraps a value in single quotes.
 *
 * Example:
 * ```
 * text("hello")
 * ```
 *
 * Produces:
 * ```
 * 'hello'
 * ```
 */
fun wrapText(from: Any?): SQLiteral =
    Wrapper(open = "'", close = "'", value = from.toLiteral())

/**
 * Wraps a value in parentheses.
 *
 * Example:
 * ```
 * wrap { value("x") }
 * ```
 *
 * Produces:
 * ```
 * (x)
 * ```
 */
fun wrapParentheses(from: Any?): SQLiteral =
    Wrapper(open = "(", close = ")", value = from.toLiteral())

/**
 * Creates a SQL function call.
 *
 * Example:
 * ```
 * function("LOWER", "name")
 * ```
 *
 * Produces:
 * ```
 * LOWER(name)
 * ```
 */
fun function(name: String, value: SQLiteral) =
    Wrapper(name = name, open = "(", close = ")", value = value.toLiteral())

/**
 * Creates a SQL function with multiple arguments.
 *
 * Example:
 * ```
 * function("COALESCE") {
 *     value("x")
 *     value("y")
 * }
 * ```
 */
fun function(name: String, build: Arguments.() -> Unit) =
    function(name, Arguments(separator = ", ").apply(build))

/**
 * Internal wrapper used to render SQL constructs such as
 * functions, quoted values, and grouped expressions.
 */
class Wrapper(
    override val name: String = "",
    val open: String = " ",
    val close: String = " ",
    value: SQLiteral
) : Expression(value = value) {

    override fun build(): String =
        "$name$open${value!!.build()}$close"
}

/**
 * Inserts a raw value into the expression.
 */
fun Expression.value(from: Any?): SQLiteral =
    updateValue(Expression(from.toLiteral()))

/**
 * Inserts the SQL wildcard (`*`).
 */
fun Expression.all(): SQLiteral =
    updateValue(all)

/**
 * Wraps the provided expression in parentheses.
 */
fun Expression.wrap(expression: () -> SQLiteral): SQLiteral =
    updateValue(wrapParentheses(expression()))

/**
 * Inserts a text literal wrapped in single quotes.
 */
fun Expression.text(from: Any?): SQLiteral =
    updateValue(wrapText(from))

/**
 * Creates a SQL list `(x, y, z)`.
 */
fun Expression.list(list: List<Any>): SQLiteral =
    updateValue(
        Wrapper(open = "(", close = ")", value = Arguments(separator = ", ", list = list))
    )

/**
 * COUNT aggregate function.
 */
fun Expression.count(value: Any): SQLiteral =
    updateFunction(name = "Count", value)

/**
 * SUM aggregate function.
 */
fun Expression.sum(value: Any = all): SQLiteral =
    updateFunction(name = "SUM", value)

/**
 * AVG aggregate function.
 */
fun Expression.avg(value: Any): SQLiteral =
    updateFunction(name = "AVG", value)

/**
 * MIN aggregate function.
 */
fun Expression.min(value: Any): SQLiteral =
    updateFunction(name = "MIN", value)

/**
 * MAX aggregate function.
 */
fun Expression.max(value: Any): SQLiteral =
    updateFunction(name = "MAX", value)

/**
 * GROUP_CONCAT aggregate function.
 *
 * Concatenates grouped values with an optional separator.
 */
fun Expression.groupConcat(value: Any, separator: String? = null): SQLiteral =
    updateFunction(name = "GROUP_CONCAT") {
        value(value)
        separator?.also { text(it) }
    }

/**
 * LENGTH string function.
 */
fun Expression.length(value: String): SQLiteral =
    updateFunction(name = "LENGTH", value)

/**
 * LOWER string function.
 */
fun Expression.lower(value: String): SQLiteral =
    updateFunction(name = "LOWER", value)

/**
 * UPPER string function.
 */
fun Expression.upper(value: String): SQLiteral =
    updateFunction(name = "UPPER", value)

/**
 * TRIM string function.
 */
fun Expression.trim(value: String): SQLiteral =
    updateFunction(name = "TRIM", value)

/**
 * LTRIM string function.
 */
fun Expression.ltrim(value: String): SQLiteral =
    updateFunction(name = "LTRIM", value)

/**
 * RTRIM string function.
 */
fun Expression.rtrim(value: String): SQLiteral =
    updateFunction(name = "RTRIM", value)

/**
 * SUBSTR string function.
 */
fun Expression.substr(value: Any, start: Int, length: Int? = null): SQLiteral =
    updateFunction(name = "SUBSTR") {
        value(value)
        text(start)
        length?.also { value(it) }
    }

/**
 * INSTR string function.
 */
fun Expression.instr(value: Any, substring: String): SQLiteral =
    updateFunction(name = "INSTR") {
        value(value)
        text(substring)
    }

/**
 * PRINTF formatting function.
 */
fun Expression.printf(format: String, vararg values: Any): SQLiteral =
    updateFunction(name = "PRINTF") {
        text(format)
        values.forEach { value(it) }
    }

/**
 * REPLACE string function.
 */
fun Expression.replace(string: Any, from: String, to: String): SQLiteral =
    updateFunction(name = "REPLACE") {
        value(string)
        text(from)
        text(to)
    }

/**
 * ABS numeric function.
 */
fun Expression.abs(value: Any): SQLiteral =
    updateFunction(name = "ABS", value = value.toLiteral())

/**
 * ROUND numeric function.
 */
fun Expression.round(value: Any, precision: Int? = null): SQLiteral =
    updateFunction(name = "ROUND") {
        value(value)
        precision?.also { value(it) }
    }

/**
 * CEIL numeric function.
 */
fun Expression.ceil(value: Any): SQLiteral =
    updateFunction(name = "CEIL", value = value.toLiteral())

/**
 * CEILING numeric function.
 */
fun Expression.ceiling(value: Any): SQLiteral =
    updateFunction(name = "CEILING", value = value.toLiteral())

/**
 * FLOOR numeric function.
 */
fun Expression.floor(value: Any): SQLiteral =
    updateFunction(name = "FLOOR", value = value.toLiteral())

/**
 * RANDOM function.
 */
fun Expression.random(): SQLiteral =
    updateFunction(name = "RANDOM", value = "".toLiteral())

/**
 * RANDOMBLOB function.
 */
fun Expression.randomBlob(size: Any): SQLiteral =
    updateFunction(name = "RANDOMBLOB", value = size.toLiteral())

/**
 * SIGN numeric function.
 */
fun Expression.sign(value: Any): SQLiteral =
    updateFunction(name = "SIGN", value = value.toLiteral())

/**
 * POWER numeric function.
 */
fun Expression.power(x: Any, y: Any): SQLiteral =
    updateFunction(name = "POWER") {
        value(x)
        value(y)
    }

/**
 * DATE function.
 */
fun Expression.date(timestamp: Any): SQLiteral =
    updateFunction(name = "DATE", value = timestamp.toLiteral())

/**
 * TIME function.
 */
fun Expression.time(timestamp: Any): SQLiteral =
    updateFunction(name = "TIME", value = timestamp.toLiteral())

/**
 * DATETIME function.
 */
fun Expression.dateTime(timestamp: Any): SQLiteral =
    updateFunction(name = "DATETIME", value = timestamp.toLiteral())

/**
 * JULIANDAY function.
 */
fun Expression.julianDay(timestamp: Any): SQLiteral =
    updateFunction(name = "JULIANDAY", value = timestamp.toLiteral())

/**
 * STRFTIME formatting function.
 */
fun Expression.strftime(format: String, timestamp: Any): SQLiteral =
    updateFunction(name = "STRFTIME") {
        text(format)
        value(timestamp)
    }

/**
 * CURRENT_DATE keyword.
 */
fun Expression.currentDate(): SQLiteral =
    updateFunction(name = "CURRENT_DATE", value = "".toLiteral())

/**
 * CURRENT_TIME keyword.
 */
fun Expression.currentTime(): SQLiteral =
    updateFunction(name = "CURRENT_TIME", value = "".toLiteral())

/**
 * CURRENT_TIMESTAMP keyword.
 */
fun Expression.currentTimestamp(): SQLiteral =
    updateFunction(name = "CURRENT_TIMESTAMP", value = "".toLiteral())

/**
 * SQL CASE expression.
 */
fun Expression.case(expr: Case.() -> Unit): SQLiteral =
    updateValue(Clause("CASE", arguments = Case().apply(expr)))

/**
 * IFNULL function.
 */
fun Expression.ifNull(x: Any?, fallback: Any): SQLiteral =
    updateFunction(name = "IFNULL") {
        value(x.toLiteral())
        value(fallback.toLiteral())
    }

/**
 * COALESCE function.
 */
fun Expression.coalesce(vararg values: Any?): SQLiteral =
    updateFunction(name = "COALESCE") {
        values.forEach { value(it) }
    }

/**
 * NULLIF function.
 */
fun Expression.nullIf(x: Any, y: Any): SQLiteral =
    updateFunction(name = "NULLIF") {
        value(x)
        value(y)
    }

/**
 * SQLite IIF conditional function.
 */
fun Expression.iIf(expr: Any, x: Any, y: Any): SQLiteral =
    updateFunction(name = "IIF") {
        value(expr)
        value(x)
        value(y)
    }

/**
 * Casts a given value to a specified SQL type using the SQL `CAST` function.
 *
 * This method applies a type conversion to the provided value and updates the current SQL expression
 * to include the cast operation.
 *
 * @param value The value to be cast to the specified SQL type.
 * @param type The target SQL type, represented by a value from the [SQLType] enum.
 * @return A new [SQLiteral] instance representing the updated expression with the applied cast operation.
 */
fun Expression.cast(value: Any, type: SQLType): SQLiteral =
    updateFunction(name = "CAST") { value AS type }

/**
 * TYPEOF function.
 */
fun Expression.typeOf(expr: Any): SQLiteral =
    updateFunction(name = "TYPEOF", value = expr.toLiteral())

/**
 * LAST_INSERT_ROWID function.
 */
fun Expression.lastInsertRowId(): SQLiteral =
    updateFunction(name = "LAST_INSERT_ROWID", value = "".toLiteral())

/**
 * CHANGES function.
 */
fun Expression.changes(): SQLiteral =
    updateFunction(name = "CHANGES", value = "".toLiteral())

/**
 * TOTAL_CHANGES function.
 */
fun Expression.totalChanges(): SQLiteral =
    updateFunction(name = "TOTAL_CHANGES", value = "".toLiteral())

/**
 * Internal helper used to update the current expression
 * with a SQL function call.
 */
fun Expression.updateFunction(name: String, value: Any): SQLiteral =
    updateValue(function(name = name, value = value.toLiteral()))

/**
 * Internal helper used to update the current expression
 * with a SQL function using a DSL block.
 */
fun Expression.updateFunction(name: String, build: Arguments.() -> Unit): SQLiteral =
    updateValue(function(name = name, build = build))