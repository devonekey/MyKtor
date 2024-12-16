package com.example.plugins

import com.example.model.FakeTaskRepository
import com.example.model.Priority
import com.example.model.Task
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApiEndpointTest {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Test
    fun testRoot() = testApplication {
        application {
            val repository = FakeTaskRepository()

            configureSerialization()
            configureRouting(repository)
        }

        val response = client.get("/")

        Assertions.assertEquals(HttpStatusCode.OK, response.status)
        Assertions.assertEquals("Hello Ktor!", response.bodyAsText())
    }

    @Test
    fun tasksCanBeFoundByPriority() = testApplication {
        application {
            val repository = FakeTaskRepository()

            configureSerialization()
            configureRouting(repository)
        }

        val priority = Priority.Medium
        val response = client.getAsJsonPath("/tasks/byPriority/$priority")
            .read<HttpResponse>("$")
        val descriptions: List<String> = JsonPath.parse(response.body<String>())
            .read("$[*].description")

        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status
        )
        assertContains(descriptions, "Mow the lawn")
        assertContains(descriptions, "Paint the fence")
    }

    @Test
    fun invalidPriorityProduces400() = testApplication {
        application {
            val repository = FakeTaskRepository()

            configureSerialization()
            configureRouting(repository)
        }

        val response = client.get("/tasks/byPriority/Invalid")

        assertEquals(
            expected = HttpStatusCode.BadRequest,
            actual = response.status
        )
    }

    @Test
    fun unusedPriorityProduces404() = testApplication {
        application {
            val repository = FakeTaskRepository()

            configureSerialization()
            configureRouting(repository)
        }

        val response = client.get("/tasks/byPriority/Vital")

        assertEquals(
            expected = HttpStatusCode.NotFound,
            actual = response.status
        )
    }

    @Test
    fun tasksCanBeFoundByName() = testApplication {
        application {
            val repository = FakeTaskRepository()

            configureSerialization()
            configureRouting(repository)
        }

        val response = client.getAsJsonPath("/tasks/byName/shopping")
            .read<HttpResponse>("$")
        val descriptions: List<String> = JsonPath.parse(response.body<String>())
            .read("$[*].description")

        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status
        )
        assertContains(descriptions, "Buy the groceries")
    }

    @Test
    fun newTasksCanBeAdded() = testApplication {
        application {
            val repository = FakeTaskRepository()

            configureSerialization()
            configureRouting(repository)
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
        val response = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(Task("Swimming", "Go to the beach", Priority.Low))
        }

        assertEquals(HttpStatusCode.NoContent, response.status)

        val response2 = client.get("/tasks")

        assertEquals(HttpStatusCode.OK, response2.status)

        val (names, descriptions) = response2.body<List<Task>>()
            .let { tasks ->
                Pair(
                    tasks.map { task -> task.name },
                    tasks.map { task -> task.description }
                )
            }

        assertContains(names, "Swimming")
        assertContains(descriptions, "Go to the beach")
    }

    @Test
    fun taskCanBeDeleted() = testApplication {
        application {
            val repository = FakeTaskRepository()

            configureSerialization()
            configureRouting(repository)
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
        val response = client.delete("/tasks/cleaning")

        assertEquals(HttpStatusCode.NoContent, response.status)

        val response2 = client.get("/tasks/byName/cleaning")

        assertEquals(HttpStatusCode.NotFound, response2.status)
    }

    suspend fun HttpClient.getAsJsonPath(url: String): DocumentContext {
        val response = this.get(url) {
            accept(ContentType.Application.Json)
        }

        logger.info("status: ${response.status}")

        return JsonPath.parse(response)
    }
}
