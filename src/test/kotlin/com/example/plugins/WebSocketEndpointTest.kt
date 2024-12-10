package com.example.plugins

import com.example.model.Task
import com.example.model.FakeTaskRepository
import com.example.model.TaskRepository
import com.example.module
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.converter
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WebSocketEndpointTest {
    lateinit var repository: TaskRepository

    @BeforeEach
    fun setup() {
        repository = FakeTaskRepository()
    }

    @Test
    fun testRoot() = testApplication {
        application { module() }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }
        val expectedTasks = repository.allTask()
        var actualTasks = emptyList<Task>()

        client.webSocket("/tasks") {
            incoming
                .consumeAsFlow()
                .map {
                    converter!!.deserialize(
                        charset = Charsets.UTF_8,
                        typeInfo = typeInfo<Task>(),
                        content = it
                    ) as Task
                }
                .scan(emptyList<Task>()) { list, task -> list + task }
                .collect { allTasks -> actualTasks = allTasks }
        }
        assertEquals(expectedTasks.size, actualTasks.size)
        expectedTasks.forEachIndexed { index, task ->
            assertEquals(task, actualTasks[index])
        }
    }
}
