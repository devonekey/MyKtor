package com.example.plugins

import com.example.model.Priority
import com.example.model.Task
import com.example.model.TaskRepository
import com.example.model.taskAsTable
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }

    routing {
        staticResources("/content", "mycontent")
        staticResources("/task-ui", "task-ui")

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

        route("/tasks") {
            get {
                call.respondText(
                    contentType = ContentType.parse("text/html"),
                    text = TaskRepository.allTask().taskAsTable()
                )
            }

            get("/byName/{taskName}") {
                val name = call.parameters["taskName"]

                if (name.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest)

                    return@get
                }

                val task = TaskRepository.taskByName(name)

                if (task == null) {
                    call.respond(HttpStatusCode.NotFound)

                    return@get
                }

                call.respondText(
                    contentType = ContentType.Text.Html,
                    text = listOf(task).taskAsTable()
                )
            }

            get("/byPriority/{priority}") {
                val priorityAsText = call.parameters["priority"]

                if (priorityAsText.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest)

                    return@get
                }

                try {
                    val priority = Priority.valueOf(priorityAsText)
                    val tasks = TaskRepository.taskByPriority(priority)

                    if (tasks.isEmpty()) {
                        call.respond(HttpStatusCode.NotFound)

                        return@get
                    }

                    call.respondText(
                        contentType = ContentType.Text.Html,
                        text = tasks.taskAsTable()
                    )
                } catch (_: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            post {
                val formContent = call.receiveParameters()
                val params = Triple(
                    formContent["name"] ?: "",
                    formContent["description"] ?: "",
                    formContent["priority"] ?: ""
                )

                if (params.toList().any { it.isBlank() }) {
                    call.respond(HttpStatusCode.BadRequest)

                    return@post
                }

                try {
                    TaskRepository.addTask(
                        Task(
                            name = params.first,
                            description = params.second,
                            priority = Priority.valueOf(params.third)
                        )
                    )

                    call.respond(HttpStatusCode.NoContent)
                } catch (_: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (_: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
