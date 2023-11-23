package com.arth.notee

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.flowOf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.coRouter

fun main(args: Array<String>) {
    runApplication<NoteApplication>(*args)
}

@SpringBootApplication
class NoteApplication {


    @Autowired
    lateinit var mapper: ObjectMapper

    @Bean
    fun hello(@Autowired mapper: ObjectMapper) = coRouter {
        GET("/hello") {
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyAndAwait(flowOf(mapper.writeValueAsString(Response(
                    "hello",
                    HttpStatus.OK.value().toString(),
                    success = true
                ))))
        }
    }

    data class Response(
        val body:String,
        val code:String,
        val success:Boolean
    )
}
