package com.example

import com.example.model.PostgresTaskRepository
import com.example.plugins.configureDatabases
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureSockets
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val repository = PostgresTaskRepository()

    configureSerialization()
    configureDatabases()
    configureRouting(repository)
    configureSockets(repository)
}
