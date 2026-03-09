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
    val left: SQLiteral,
    val right: SQLiteral,
    val separator: String = " "
) : Expression() {


    /**
     * Builds a SQL expression string by concatenating the `build` results of the left and right operands,
     * separated by the operator name and a defined separator.
     *
     * This method constructs the expression in the format:
     * `<left operand><separator><operator name><separator><right operand>`.
     *
     * @return A string representing the SQL expression combining the left operand, operator name, and right operand,
     * formatted using the specified separator.
     */
    override fun build() = "${left.build()}$separator$name$separator${right.build()}"

    /**
     * Retrieves the right-hand operand of the operator.
     *
     * @return The right-hand operand of the operator as an instance of `SQLiteral`,
     * or `null` if there is no operand.
     */
    override fun value(): SQLiteral? {
        return right
    }
}