plugins {
    kotlin("jvm")
    `java-library`
    `maven-publish`
}

group = "h.ezz"
version = "1.0.0"

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

            groupId = "h.ezz"
            artifactId = "sqlQueryDSL"
            version = "1.0.0"
        }
    }
}