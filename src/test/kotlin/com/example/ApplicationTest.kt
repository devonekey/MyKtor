package com.example

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }

        val response = client.get("/")

        Assertions.assertEquals(HttpStatusCode.OK, response.status)
        Assertions.assertEquals("Hello Ktor!", response.bodyAsText())
    }

    @Test
    fun tasksCanBeFoundByPriority() = testApplication {
        application { module() }

        val response = client.get("/tasks/byPriority/Medium")
        val body = response.bodyAsText()

        assertEquals(
            expected = HttpStatusCode.OK,
            actual = response.status
        )
        assertContains(body, "Mow the lawn")
        assertContains(body, "Paint the fence")
    }

    @Test
    fun invalidPriorityProduces400() = testApplication {
        application { module() }

        val response = client.get("/tasks/byPriority/Invalid")

        assertEquals(
            expected = HttpStatusCode.BadRequest,
            actual = response.status
        )
    }

    @Test
    fun unusedPriorityProduces404() = testApplication {
        application { module() }

        val response = client.get("/tasks/byPriority/Vital")

        assertEquals(
            expected = HttpStatusCode.NotFound,
            actual = response.status
        )
    }

    @Test
    fun newTasksCanBeAdded() = testApplication {
        application { module() }

        val response = client.post("/tasks") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                listOf(
                    "name" to "Swimming",
                    "description" to "Go to the beach",
                    "priority" to "Low"
                ).formUrlEncode()
            )
        }

        assertEquals(HttpStatusCode.NoContent, response.status)

        val response2 = client.get("/tasks")

        assertEquals(HttpStatusCode.OK, response2.status)

        val body = response2.bodyAsText()

        assertContains(body, "Swimming")
        assertContains(body, "Go to the beach")
    }
}
