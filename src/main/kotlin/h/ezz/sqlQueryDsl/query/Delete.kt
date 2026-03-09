package h.ezz.sqlQueryDsl.query

import h.ezz.sqlQueryDsl.components.DMLStatement

class Delete() : DMLStatement("DELETE FROM", "") {


    override fun build(): String {
        return StringBuilder()
            .apply {
                append(name)
                append(" ")
                table?.also { append(it.build()) }
                where?.also {
                    append(lineSeparator)
                    append(it.build())
                }
                returning?.also {
                    append(lineSeparator)
                    append(it.build())
                }
            }
            .toString()
    }
}