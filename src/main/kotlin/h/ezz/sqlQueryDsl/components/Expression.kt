package h.ezz.sqlQueryDsl.components

import h.ezz.sqlQueryDsl.queryBuilder


/**
 * Represents an SQL expression used to build dynamically composable SQL queries.
 *
 * The `Expression` class serves as a base for creating SQL literals and operations.
 * It provides a variety of SQL-compatible operators, such as logical, comparison, and arithmetic operators,
 * along with mechanisms to chain or combine expressions.
 *
 * All operations return an instance of `SQLiteral`, ensuring all expressions can conform to
 * SQL's structural requirements and be built into valid SQL strings using the `build` method.
 *
 * @property value The current SQLiteral representation of the expression value, which can be updated or extended.
 */
@SQLQueryMaker
open class Expression(protected open var value: SQLiteral? = null) : SQLiteral {
    override fun build(): String = value?.build() ?: Value(value = value).build()

    infix fun Any.IN(right: Any?): SQLiteral = setOperator(name = "IN", right = right)
    infix fun Any.LIKE(right: Any?): SQLiteral = setOperator(name = "LIKE", right = right)
    infix fun Any.IS(right: Any??): SQLiteral = setOperator(name = "IS", right = right)
    infix fun Any.AND(right: Any?): SQLiteral = setOperator(name = "AND", right = right)
    infix fun Any.OR(right: Any?): SQLiteral = setOperator(name = "OR", right = right)
    infix fun Any.NOT(right: Any?): SQLiteral = setOperator(name = "NOT", right = right)
    infix fun Any.AS(right: Any?): SQLiteral = setOperator(name = "AS", right = right)
    infix fun Any.LESS(right: Any?): SQLiteral = setOperator(name = "<", right = right)
    infix fun Any.GREATER(right: Any?): SQLiteral = setOperator(name = ">", right = right)
    infix fun Any.LESS_OR_EQL(right: Any?): SQLiteral = setOperator(name = "<=", right = right)
    infix fun Any.GREATER_OR_EQL(right: Any?): SQLiteral = setOperator(name = ">=", right = right)
    infix fun Any.PLS(right: Any?): SQLiteral = setOperator(name = "+", right = right)
    infix fun Any.MOD(right: Any?): SQLiteral = setOperator(name = "%", right = right)
    infix fun Any.DIV(right: Any?): SQLiteral = setOperator(name = "/", right = right)
    infix fun Any.X(right: Any?): SQLiteral = setOperator(name = "*", right = right)
    infix fun Any.EQL(right: Any?): SQLiteral = setOperator(name = "=", right = right)
    infix fun Any.BETWEEN(right: Any?): SQLiteral = setOperator(name = "BETWEEN", right = right)
    infix fun Any.ANY(value: Any?): SQLiteral = setOperator(name = "ANY", right = value)
    infix fun Any.ALL(value: Any?): SQLiteral = setOperator(name = "ALL", right = value)
    infix fun Any.EXISTS(value: Any?): SQLiteral = setOperator(name = "EXISTS", right = value)
    infix fun Any.CONCAT(value: Any?): SQLiteral = setOperator(name = "||", right = value)

    /**
     * Updates the current instance with a new `Operator` constructed using the
     * provided operator name, the current object, and a right-hand operand. The
     * resulting `Operator` defines a SQL operation between the left-hand and
     * right-hand sides with the specified name.
     *
     * @param name The name of the operator to be used (e.g., "AND", "OR", "EQL").
     * @param right The right-hand operand for the operator, to be converted into a [SQLiteral].
     * @return The newly created [SQLiteral] instance representing the updated operator.
     */
    protected fun Any.setOperator(name: String, right: Any?): SQLiteral {
        val left  = if (this@Expression == this) (this as Expression).value() else this.toLiteral()
        return updateValue(
            Operator(
                name = name,
                left = left,
                right = right.toLiteral()
            )
        )
    }


    /**
     * Updates the current value of the instance with the provided SQLiteral and returns the new value.
     *
     * @param new The new [SQLiteral] to update the value of the instance.
     * @return The updated [SQLiteral] that has been set as the new value.
     */
    open fun updateValue(new: SQLiteral): SQLiteral {
        value = new
        return new
    }

    open fun value(): SQLiteral? = value

}