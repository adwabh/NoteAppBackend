package com.arth.notee.controller

import com.arth.notee.exception.MESSAGE_GENERIC
import com.arth.notee.network.NoteResponse
import com.arth.notee.network.NotesListResponse
import com.arth.notee.network.Response
import com.arth.notee.repository.NotesCoRoutinesRepository
import com.arth.notee.repository.Notes
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.ErrorResponse
import org.springframework.web.reactive.function.server.*
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.sql.Timestamp
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.util.TimeZone
import java.util.UUID

@Configuration
class NotesConfig {

    @Bean
    fun http(@Autowired repo: NotesCoRoutinesRepository): RouterFunction<ServerResponse> {

        return coRouter {
            GET("/v1/notes") {
                val destination = mutableListOf<NoteResponse>()

                val transform: suspend (value: Notes) -> NoteResponse = {
                    it.toResponse()!!
                }
                val notes = repo.findAll().map(transform).toList(destination)
                ServerResponse.ok().bodyValue(Response.success("", NotesListResponse(notes))).awaitSingle()
            }

            GET("/v1/note/{id}") {
                val note = repo.findById(it.pathVariable("id"))
                val body = note.toResponse()
                val response = if (body != null) {
                    ServerResponse.ok().bodyValueAndAwait(Response.success("", body))
                } else {
                    val builder = org.springframework.web.ErrorResponse.builder(
                        Exception(), HttpStatus.NOT_FOUND, MESSAGE_GENERIC
                    ).build()
                    ServerResponse.from(builder).awaitSingle()
                }
                response
            }

            POST("/v1/note") {
                with(it.headers()) {
                    if (header("title").isNotEmpty() ||
                        header("body").isNotEmpty()) {
                        val note = Notes(
                            null,
                            it.headers().header("title").first(),
                            it.headers().header("body").first(),
                            OffsetDateTime.now(),
                            OffsetDateTime.now()
                            )
                        val id = repo.save(note)
                        ServerResponse.ok().bodyValue(Response.success("S200", id)).awaitSingle()
                    } else {
                        ServerResponse.from(
                            ErrorResponse.builder(
                                IllegalArgumentException(),
                                HttpStatus.BAD_REQUEST,
                                "empty value for inputs"
                            ).build()
                        ).awaitSingle()
                    }
                }
            }

            PUT("v1/note/{id}") {
                val entity = repo.findById(it.pathVariable("id"))
                with(it.headers()){
                    if (header("title").isNotEmpty() ||
                        header("body").isNotEmpty()) {
                        if (entity != null) {
                            val update = entity.copy(title = header("title").first(), body = header("body").first())
                            val id = repo.save(update)
                            ServerResponse.ok().bodyValueAndAwait(Response.success("S200", id))
                        } else {
                            ServerResponse.from(
                                ErrorResponse.builder(
                                    IllegalArgumentException(),
                                    HttpStatus.NO_CONTENT,
                                    "Something went wrong"
                                ).build()
                            ).awaitSingle()
                        }
                    } else {
                        ServerResponse.from(
                            ErrorResponse.builder(
                                IllegalArgumentException(),
                                HttpStatus.BAD_REQUEST,
                                "empty value for inputs"
                            ).build()
                        ).awaitSingle()
                    }
                }
            }

            DELETE("/v1/note/{id}") {
                val id = repo.findById(it.pathVariable("id"))
                if (id != null) {
                    val deleted = repo.delete(id)
                    ServerResponse.ok().bodyValueAndAwait(Response.success("", deleted))
                } else {
                    ServerResponse.notFound().buildAndAwait()
                }
            }
        }
    }
}

fun Notes?.toResponse(): NoteResponse? {
    return this?.let {
        NoteResponse(
            it.id?.toString()?:"", it.title, it.body, it.createdDate.toEpochSecond(), it.updatedDate.toEpochSecond()
        )
    }
}
