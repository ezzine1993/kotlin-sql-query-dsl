package h.ezz.sqlQueryDsl.components

/**
 * Represents an operator-based SQL expression.
 *
 * The `Operator` class extends the `Expression` class to provide support
 * for SQL expressions involving a binary operator. It combines a `left`
 * operand, a `right` operand, and a named operator to construct a SQL
 * fragment. The operands (`left` and `right`) are instances of `SQLiteral`,
 * and the operator name is defined by the `name` property.
 *
 * @property name The name of the operator (e.g., "+", "AND", "EQL").
 * @property left The left-hand operand of the operator, represented as a `SQLiteral`.
 * @property right The right-hand operand of the operator, represented as a `SQLiteral`.
 * @property separator The separator string used to format the SQL expression (default is a space).
 *
 * @constructor Creates an instance of the `Operator` class with the specified operator name,
 * left-hand operand, right-hand operand, and separator.
 */
open class Operator(
    override val name: String,
    val left: SQLiteral?,
    val right: SQLiteral,
    val separator: String = " "
) : Expression() {


    /**
     * Builds a SQL expression string by concatenating the left operand, operator name, and right operand.
     *
     * Combines the `left` operand's SQL representation, the operator name (`name`),
     * and the `right` operand's SQL representation, separating the `left` and the `name`
     * with the defined `separator`. If `left` is null, it omits it from the output.
     *
     * @return The constructed SQL expression as a `String`.
     */
    override fun build(): String =
        StringBuilder().apply {
            left?.also {
                append(it.build())
                append(separator)
            }
            append(name)
            append(separator)
            append(right.build())
        }.toString()

    /**
     * Retrieves the right-hand operand of the operator.
     *
     * @return The right-hand operand of the operator as an instance of `SQLiteral`,
     * or `null` if there is no operand.
     */
    override fun value(): SQLiteral? {
        return left
    }
}