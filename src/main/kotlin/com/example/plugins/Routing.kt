package com.example.plugins

import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }

    routing {
        staticResources("/content", "mycontent")

        get("/") {
            call.respondText("Hello Ktor!", ContentType.Text.Plain)
        }

        get("/test1") {
            val text = "<h1>Hello From Ktor</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }

        get("/error-test") {
            throw IllegalStateException("Too Busy")
        }

        get("tasks") {
            call.respondText(
                contentType = ContentType.parse("text/html"),
                text = """
                    <h3>TODO</h3>
                    <ol>
                        <li>A table of all the tasks</li>
                        <li>A form of submit new tasks</li>
                    </ol>
                """.trimIndent()
            )
        }
    }
}
