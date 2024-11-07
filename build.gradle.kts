plugins {
    application
    kotlin("jvm") version "2.0.20"
    id("io.ktor.plugin") version "3.0.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.example.ApplicationKt")
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(14)
}
