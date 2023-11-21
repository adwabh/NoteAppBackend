package com.arth.notee

import kotlinx.coroutines.flow.flowOf
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.coRouter

fun main(args: Array<String>) {
    runApplication<NoteApplication>(*args)
}

@SpringBootApplication
class NoteApplication {
    @Bean
    fun hello() = coRouter {
        GET("/hello") {
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyAndAwait(flowOf("hello"))
        }
    }
}
