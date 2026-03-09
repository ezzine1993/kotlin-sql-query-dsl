# Kotlin SQL Query DSL

A lightweight Kotlin DSL for building SQL statements in a more readable and composable way.

This library lets you generate SQL strings using Kotlin builders instead of concatenating raw SQL manually.

## Features

- Kotlin-first DSL
- Build `SELECT`, `INSERT`, `UPDATE`, and `DELETE`
- Common SQL operators as infix functions
- Built-in helpers for aggregates, string functions, date/time functions, and `CASE`
- Optional pretty formatting for readable output
- Small API surface and no heavy dependencies

## Installation

### **1. Add JitPack repository**

Gradle (Kotlin DSL):

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io")
}
```

Gradle (Groovy):

```Groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

### **1. Add Dependency**

Gradle (Kotlin DSL):

```kotlin
dependencies {
    implementation("com.github.ezzine1993:Kotlin-SQL-Query-DSL:1.0.0")
}
```

Gradle (Groovy):

```Groovy
dependencies {
    implementation("com.github.ezzine1993:Kotlin-SQL-Query-DSL:1.0.0")
}
```



## Quick start

```kotlin
val sql = queryBuilder(pretty = true) {
    select {
        projections {
            value("id")
            value("name")
        }

        from {
            value("users")
        }

        where {
            "active" EQL 1
        }
    }
}.build()

println(sql)
```

Output:

```sql
SELECT id, name
FROM users
WHERE active = 1
```

---

## Basic usage

The entry point is `queryBuilder { ... }`.

```kotlin
val query = queryBuilder {
    select {
        projections { all() }
        from { value("users") }
    }
}.build()
```

You can enable pretty output:

```kotlin
val query = queryBuilder(pretty = true) {
    select {
        projections { all() }
        from { value("users") }
    }
}.build()
```

---

## SELECT

### Simple select

```kotlin
val sql = queryBuilder {
    select {
        projections {
            value("id")
            value("name")
            value("email")
        }
        from {
            value("users")
        }
    }
}.build()
```

```sql
SELECT id, name, email FROM users
```

### Select all

```kotlin
val sql = queryBuilder {
    select {
        projections { all() }
        from { value("users") }
    }
}.build()
```

```sql
SELECT * FROM users
```

### WHERE

```kotlin
val sql = queryBuilder {
    select {
        projections { all() }
        from { value("users") }
        where {
            wrap { ("age" GREATER_OR_EQL 18) AND ("status" EQL "active") }
        }
    }
}.build()
```

### ORDER BY

```kotlin
val sql = queryBuilder {
    select {
        projections {
            value("id")
            value("name")
        }
        from { value("users") }
        orderBy {
            desc("created_at")
            asc("name")
        }
    }
}.build()
```

```sql
SELECT id, name FROM users ORDER BY created_at DESC, name ASC
```

### GROUP BY + HAVING

```kotlin
val sql = queryBuilder {
    select {
        projections {
            value("department")
            count("*")
        }
        from { value("employees") }
        groupBy {
            value("department")
        }
        having {
            count("*") GREATER 5
        }
    }
}.build()
```

### LIMIT + OFFSET

```kotlin
val sql = queryBuilder {
    select {
        projections { all() }
        from { value("posts") }
        limit(10)
        offset(20)
    }
}.build()
```

### JOIN

```kotlin
val sql = queryBuilder(pretty = true) {
    select {
        projections {
            value("u.id")
            value("u.name")
            value("p.title")
        }
        from {
            value("users u")
            join {
                value("posts p") ON ("u.id" EQL "p.user_id")
            }
        }
    }
}.build()
```

### UNION / UNION ALL / INTERSECT / EXCEPT

```kotlin
val sql = queryBuilder {
    select {
        projections { value("name") }
        from { value("customers") }
    } UNION select {
        projections { value("name") }
        from { value("suppliers") }
    }
}.build()
```

---

## INSERT

### Insert values

```kotlin
val sql = queryBuilder(pretty = true) {
    insertInto {
        table("users")
        columns("name", "email", "age")
        values("Mario", "mario@example.com", 32)
    }
}.build()
```

```sql
INSERT INTO users
(name, email, age)
VALUES ('Mario', 'mario@example.com', 32)
```

### Insert from select

```kotlin
val sql = queryBuilder(pretty = true) {
    insertInto {
        table("archived_users")
        columns("id", "name")
        select {
            projections {
                value("id")
                value("name")
            }
            from { value("users") }
            where {
                "deleted" EQL 1
            }
        }
    }
}.build()
```

### Returning inserted columns

```kotlin
val sql = queryBuilder(pretty = true) {
    insertInto {
        table("users")
        columns("name", "email")
        values("Alice", "alice@example.com")
        returning("id", "name")
    }
}.build()
```

---

## UPDATE

```kotlin
val sql = queryBuilder(pretty = true) {
    update {
        table("users")
        set {
            "name" EQL text("Updated Name")
            "age" EQL 33
        }
        where {
            "id" EQL 10
        }
        returning("id", "name", "age")
    }
}.build()
```

```sql
UPDATE users
SET name = 'Updated Name', age = 33
WHERE id = 10
RETURNING id, name, age
```

---

## DELETE

```kotlin
val sql = queryBuilder(pretty = true) {
    deleteFrom {
        table("users")
        where {
            "id" EQL 10
        }
        returning("id")
    }
}.build()
```

```sql
DELETE FROM users
WHERE id = 10
RETURNING id
```

---

## Operators

The DSL exposes many SQL operators as infix functions on expressions.

### Comparison

```kotlin
"id" EQL 1
"age" GREATER 18
"age" GREATER_OR_EQL 18
"price" LESS 100
"price" LESS_OR_EQL 100
"name" LIKE text("%john%")
"deleted_at" IS null
```

### Logical

```kotlin
("age" GREATER_OR_EQL 18) AND ("status" EQL text("active"))
("role" EQL text("admin")) OR ("role" EQL text("editor"))
```

### Other operators

```kotlin
"id" IN list(1, 2, 3)
"score" BETWEEN "10 AND 20"
"first_name" CONCAT text(" ")
```

### Arithmetic

```kotlin
"price" PLS 10
"amount" DIV 2
"counter" MOD 5
"quantity" X 3
```

---

## Functions

The DSL includes helpers for common SQL functions.

### Aggregate functions

```kotlin
count("*")
sum("amount")
avg("amount")
min("amount")
max("amount")
groupConcat("name")
```

### String functions

```kotlin
lower("name")
upper("name")
length("name")
trim("name")
ltrim("name")
rtrim("name")
substr("name", 1, 3)
instr("email", "@")
replace("email", "old.com", "new.com")
printf("%s - %s", "name", "surname")
```

### Numeric functions

```kotlin
abs(-10)
round(10.567, 2)
ceil(10.2)
ceiling(10.2)
floor(10.9)
random()
randomBlob(16)
sign(-1)
power(2, 8)
```

### Date/time functions

```kotlin
date("now")
time("now")
dateTime("now")
julianDay("now")
strftime("%Y-%m-%d", "now")
currentDate()
currentTime()
currentTimestamp()
```

### Null handling and conditional functions

```kotlin
ifNull("nickname", "Guest")
coalesce(null, "fallback", "default")
nullIf("a", "b")
iIf("age > 18", "adult", "minor")
```

### CAST / TYPEOF

```kotlin
cast("price", SQLType.INTEGER)
typeOf("price")
```

### Utility functions

```kotlin
lastInsertRowId()
changes()
totalChanges()
```

---

## CASE expression

```kotlin
val sql = queryBuilder {
    select {
        projections {
            case {
                WHEN { "age" LESS 18 THEN "minor" }
                WHEN { "age" GREATER_OR_EQL 18 THEN "adult" }
                ELSE("unknown")
            }
        }
        from { value("users") }
    }
}.build()
```

---

## Aliasing with `AS`

```kotlin
val sql = queryBuilder {
    select {
        projections {
            "name" AS "user_name"
            count("*") AS "total"
        }
        from { value("users") }
    }
}.build()
```

---

## Notes

- The DSL generates SQL strings; it does not execute queries.
- Strings passed with `value(...)` are treated as raw SQL fragments.
- Strings passed with `text(...)` are wrapped in single quotes.
- Some helpers are especially convenient for SQLite-style SQL functions.
- When composing complex boolean expressions, using `wrap { ... }` makes the final SQL clearer.

## Example

```kotlin
val sql = queryBuilder(pretty = true) {
    select {
        projections {
            value("u.id")
            value("u.name")
            count("p.id") AS "post_count"
        }

        from {
            value("users u")
            leftJoin {
                value("posts p") ON ("u.id" EQL "p.user_id")
            }
        }

        where {
            wrap {
                ("u.active" EQL 1) AND ("u.deleted_at" IS null)
            }
        }

        groupBy {
            value("u.id")
            value("u.name")
        }

        having {
            count("p.id") GREATER 0
        }

        orderBy {
            desc("post_count")
            asc("u.name")
        }

        limit(20)
    }
}.build()
```

---

