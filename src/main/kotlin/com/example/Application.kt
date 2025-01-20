package com.example

import com.example.model.PostgresTaskRepository
import com.example.plugins.configureDatabases
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureSockets
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.util.Properties

fun main(args: Array<String>) {
    embeddedServer(
        factory = Netty,
        environment = applicationEnvironment { log = LoggerFactory.getLogger(Application::class.java) },
        configure = { envConfig() },
        module = Application::module
    ).start(wait = true)
}

fun ApplicationEngine.Configuration.envConfig() {
    val properties = Properties()

    properties.load(FileInputStream("config.properties"))

    val KEY_STORE = properties.getProperty("KEY_STORE")
    val KEY_STORE_PASSWORD = properties.getProperty("KEY_STORE_PASSWORD")
    val KEY_ALIAS = properties.getProperty("KEY_ALIAS")
    val PRIVATE_KEY_PASSWORD = properties.getProperty("KEY_STORE_PASSWORD")
    val keyStoreFile = File(KEY_STORE)
    val keyStore = buildKeyStore {
        certificate(KEY_ALIAS) {
            password = PRIVATE_KEY_PASSWORD
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }

    keyStore.saveToFile(keyStoreFile, KEY_STORE_PASSWORD)

    connector { port = 8080 }
    sslConnector(
        keyStore = keyStore,
        keyAlias = KEY_ALIAS,
        keyStorePassword = { KEY_STORE_PASSWORD.toCharArray() },
        privateKeyPassword = { PRIVATE_KEY_PASSWORD.toCharArray() }
    ) {
        port = 8443
        keyStorePath = keyStoreFile
    }
}

fun Application.module() {
    val repository = PostgresTaskRepository()

    configureSerialization()
    configureDatabases()
    configureRouting(repository)
    configureSockets(repository)
}
