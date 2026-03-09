package h.ezz.app

import  h.ezz.sqlQueryDsl.queryBuilder


fun main() {
    val query = queryBuilder {
        deleteFrom {
            table("user")
            where { "id" EQL 1 }
            returning("id", "name")

        }
    }

    System.err.println(query.build())
}