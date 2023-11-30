package com.arth.notee.controller

import com.arth.notee.exception.MESSAGE_GENERIC
import com.arth.notee.network.NoteResponse
import com.arth.notee.network.NotesListResponse
import com.arth.notee.network.Response
import com.arth.notee.repository.NotesCoRoutinesRepository
import com.arth.notee.repository.NotesEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.*
import java.lang.Exception
import java.time.OffsetDateTime

@Configuration
class NotesConfig {

    @Bean
    fun http(@Autowired repo: NotesCoRoutinesRepository): RouterFunction<ServerResponse> {

        return coRouter {
            val updateOrSaveBlock: suspend (ServerRequest) -> ServerResponse = {
                val predicate: (ServerRequest.Headers) -> Boolean =
                    { it.header("title").isNotEmpty() || it.header("body").isNotEmpty() }
                val entity = if (it.method() == HttpMethod.POST) {
                    NotesEntity(
                        it.pathVariable("id"),
                        it.headers().header("title").first(),
                        it.headers().header("body").first(),
                        OffsetDateTime.now().toEpochSecond(),
                        OffsetDateTime.now().toEpochSecond()
                    )
                } else if (it.method() == HttpMethod.PUT) {
                    repo.findById(it.pathVariable("id"))
                } else {
                    null
                }
                val successResponse: suspend (ServerRequest) -> ServerResponse = {
                    if (entity != null) {
                        val id = repo.save(entity)
                        ServerResponse.ok().bodyValueAndAwait(Response.success("", id))
                    } else {
                        ServerResponse.noContent().buildAndAwait()
                    }
                }
                if(predicate(it.headers())) {
                    successResponse(it)
                } else {
                    ServerResponse.badRequest().buildAndAwait()
                }
            }

            GET("/v1/notes") {
                val destination = mutableListOf<NoteResponse>()

                val transform: suspend (value: NotesEntity) -> NoteResponse = {
                    NoteResponse(
                        it.id, it.title, it.body, it.createdDate, it.updatedDate
                    )
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

            POST("/v1/note/{id}", updateOrSaveBlock)

            PUT("v1/note/{id}", updateOrSaveBlock)

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

private fun NotesEntity?.toResponse(): NoteResponse? {
    return this?.let {
        NoteResponse(
            it.id, it.title, it.body, it.createdDate, it.updatedDate
        )
    }
}
