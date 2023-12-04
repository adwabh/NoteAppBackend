package com.arth.notee.controller

import com.arth.notee.exception.MESSAGE_GENERIC
import com.arth.notee.network.NoteResponse
import com.arth.notee.network.NotesListResponse
import com.arth.notee.network.Response
import com.arth.notee.repository.Notes
import com.arth.notee.repository.NotesCoRoutinesRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.ErrorResponse
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.*
import springfox.documentation.oas.annotations.EnableOpenApi
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.time.OffsetDateTime


// https://github.com/springdoc/springdoc-openapi-demos/tree/2.x refer this and implement

@Configuration
@EnableSwagger2
@EnableOpenApi
class NotesConfig : WebFluxConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/swagger-ui.html**")
            .addResourceLocations("classpath:/META-INF/resources/swagger-ui.html")
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/")
    }

    @Bean
    fun http(@Autowired repo: NotesCoRoutinesRepository): RouterFunction<ServerResponse> {
        return coRouter {

            ("/v1").nest {

                getAllNotes(repo)

                getNoteById(repo)

                createNote(repo)

                updateNote(repo)

                deleteNote(repo)
            }
        }
    }

    @Operation(
        method = "DELETE", summary = "Delete note", description = "Delete note"
    )
    @ApiResponse(responseCode = "200", description = "Note deleted")
    private fun CoRouterFunctionDsl.deleteNote(repo: NotesCoRoutinesRepository) {
        DELETE("/note/{id}") {
            val id = repo.findById(it.pathVariable("id"))
            if (id != null) {
                val deleted = repo.delete(id)
                ServerResponse.ok().bodyValueAndAwait(Response.success("", deleted))
            } else {
                ServerResponse.notFound().buildAndAwait()
            }
        }
    }

    @Operation(
        method = "PUT", summary = "Update note", description = "Update note"
    )
    @ApiResponse(responseCode = "200", description = "Note updated")
    private fun CoRouterFunctionDsl.updateNote(repo: NotesCoRoutinesRepository) {
        PUT("/note/{id}") {
            val entity = repo.findById(it.pathVariable("id"))
            with(it.headers()) {
                if (header("title").isNotEmpty() || header("body").isNotEmpty()) {
                    if (entity != null) {
                        val update = entity.copy(title = header("title").first(), body = header("body").first())
                        val id = repo.save(update)
                        ServerResponse.ok().bodyValueAndAwait(Response.success("S200", id))
                    } else {
                        ServerResponse.from(
                            ErrorResponse.builder(
                                IllegalArgumentException(), HttpStatus.NO_CONTENT, "Something went wrong"
                            ).build()
                        ).awaitSingle()
                    }
                } else {
                    ServerResponse.from(
                        ErrorResponse.builder(
                            IllegalArgumentException(), HttpStatus.BAD_REQUEST, "empty value for inputs"
                        ).build()
                    ).awaitSingle()
                }
            }
        }
    }

    @Operation(
        method = "POST", summary = "Create note", description = "Create note"
    )
    @ApiResponse(responseCode = "200", description = "Note created")
    private fun CoRouterFunctionDsl.createNote(repo: NotesCoRoutinesRepository) {
        POST("/note") {
            with(it.headers()) {
                if (header("title").isNotEmpty() || header("body").isNotEmpty()) {
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
                            IllegalArgumentException(), HttpStatus.BAD_REQUEST, "empty value for inputs"
                        ).build()
                    ).awaitSingle()
                }
            }
        }
    }

    @Operation(
        method = "GET",
        summary = "Get note by id",
        description = "Get note by id",
        responses = [ApiResponse(responseCode = "200", description = "Note found"), ApiResponse(
            responseCode = "404", description = "Note not found"
        )],
        requestBody = RequestBody(description = "Note id", required = true)
    )
    private fun CoRouterFunctionDsl.getNoteById(repo: NotesCoRoutinesRepository) {
        GET("/note/{id}") {
            val note = repo.findById(it.pathVariable("id"))
            val body = note.toResponse()
            val response = if (body != null) {
                ServerResponse.ok().bodyValueAndAwait(Response.success("", body))
            } else {
                val builder = ErrorResponse.builder(
                    Exception(), HttpStatus.NOT_FOUND, MESSAGE_GENERIC
                ).build()
                ServerResponse.from(builder).awaitSingle()
            }
            response
        }
    }

    @Operation(
        method = "GET", summary = "Get all notes", description = "Get all notes"
    )
    private fun CoRouterFunctionDsl.getAllNotes(repo: NotesCoRoutinesRepository) {
        GET("/notes") {
            val destination = mutableListOf<NoteResponse>()

            val transform: suspend (value: Notes) -> NoteResponse = {
                it.toResponse()!!
            }
            val notes = repo.findAll().map(transform).toList(destination)
            ServerResponse.ok().bodyValue(Response.success("", NotesListResponse(notes))).awaitSingle()
        }
    }
}

fun Notes?.toResponse(): NoteResponse? {
    return this?.let {
        NoteResponse(
            it.id?.toString() ?: "", it.title, it.body, it.createdDate.toEpochSecond(), it.updatedDate.toEpochSecond()
        )
    }
}
