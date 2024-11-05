val junit_version: String by project
val kotlin_version: String by project
val ktor_version: String by project
val logback_version: String by project
val slf4j_version: String by project
val yaml_version: String by project

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
    mainClass.set("io.ktor.server.netty.EngineMain")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-config-yaml:$yaml_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("org.slf4j:slf4j-api:$slf4j_version")

    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(14)
}
