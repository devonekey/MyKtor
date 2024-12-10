package com.example

import com.example.model.FakeTaskRepository
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureSockets
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    val repository = FakeTaskRepository()

    configureSerialization()
    configureRouting(repository)
    configureSockets(repository)
}
