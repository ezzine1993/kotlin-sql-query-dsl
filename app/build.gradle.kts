plugins {
    kotlin("jvm")
    application
}

group = "h.ezz"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":sqlQueryDSL"))
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "h.ezz.app.MainKt"
    println(mainClass.get())
}