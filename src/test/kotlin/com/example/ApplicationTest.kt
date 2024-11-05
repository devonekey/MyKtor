package com.example

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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
}
