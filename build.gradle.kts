plugins {
    kotlin("jvm") version "1.9.25"
    `java-library`
    `maven-publish`
}

group = "com.github.ezzine1993"
version = "1.0.8"

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
        create<MavenPublication>("mavenJava"){
            groupId = "com.github.ezzine1993"
            artifactId = "kotlin-sql-query-dsl"
            from(components["java"])
        }
    }
}