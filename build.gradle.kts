plugins {
    kotlin("jvm") version "1.9.25"
    `java-library`
    `maven-publish`
}

group = "com.github.ezzine1993"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

publishing {
    publications {

        create<MavenPublication>("release") {
            from(components["java"])
            from(components["release"])

            groupId = "com.github.ezzine1993"
            artifactId = "kotlin-sql-query-dsl"
            version = "1.0.1"
        }
    }
}