package com.example.plugins

import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import java.io.FileInputStream
import java.util.Properties

fun Application.configureDatabases() {
    val properties = Properties()

    properties.load(FileInputStream("config.properties"))

    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres",
        user = "postgres",
        password = properties.getProperty("DB_PASSWORD")
    )
}
