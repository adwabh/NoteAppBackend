package com.arth.notee.service

import com.arth.notee.exception.MESSAGE_GENERIC
import com.arth.notee.network.NoteResponse
import com.arth.notee.network.NotesListResponse
import com.arth.notee.network.Response
import com.arth.notee.network.converter.Converters.toResponse
import com.arth.notee.repository.Notes
import com.arth.notee.repository.NotesCoRoutinesRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.headers.Header
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.ErrorResponse
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import java.time.OffsetDateTime

@Service
class NoteService(@Autowired val repo: NotesCoRoutinesRepository) {
    @Operation(
        method = "DELETE",
        summary = "Delete note",
        description = "Delete note",
        responses = [ApiResponse(responseCode = "200", description = "Note deleted"), ApiResponse(
            responseCode = "404", description = "Note not found"
        )],
        parameters = [Parameter(
            `in` = ParameterIn.PATH,
            name = "id",
            description = "id of note to be deleted",
            required = true,
            example = "1"
        )]
    )
    suspend fun deleteNote(
        it: ServerRequest
    ): ServerResponse {
        val id = repo.findById(it.pathVariable("id"))
        return if (id != null) {
            val deleted = repo.delete(id)
            ServerResponse.ok().bodyValueAndAwait(Response.success("", deleted))
        } else {
            ServerResponse.notFound().buildAndAwait()
        }
    }

    @Operation(
        method = "PUT",
        summary = "Update note",
        description = "Update note",
        responses = [ApiResponse(responseCode = "200", description = "Note updated")]
    )
    suspend fun updateNote(it: ServerRequest): ServerResponse {
        val entity = repo.findById(it.pathVariable("id"))
        return with(it.headers()) {
            if (header("title").isNotEmpty() || header("body").isNotEmpty()) {
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
                    ErrorResponse
                        .builder(
                        IllegalArgumentException(),
                            HttpStatus.BAD_REQUEST,
                            "empty value for inputs"
                    ).build()
                ).awaitSingle()
            }
        }
    }

    @Operation(
        method = "POST",
        summary = "Create note",
        description = "Create note",
        responses = [ApiResponse(responseCode = "200", description = "Note created")],
        requestBody = RequestBody(description = "Note title and body", required = true)
    )
    suspend fun createNote(it: ServerRequest): ServerResponse = with(it.headers()) {
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

    @Operation(
        method = "GET",
        summary = "Get note by id",
        description = "Get note by id",
        responses = [ApiResponse(responseCode = "200", description = "Note found"), ApiResponse(
            responseCode = "404", description = "Note not found"
        )]
    )
    suspend fun getNoteById(it: ServerRequest): ServerResponse {
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
        return response
    }

    @Operation(
        method = "GET",
        summary = "Get all notes",
        description = "Get all notes",
        responses = [ApiResponse(responseCode = "200", description = "Notes found"), ApiResponse(
            responseCode = "404", description = "Notes not found"
        )]
    )
    suspend fun getAllNotes(request: ServerRequest): ServerResponse {
        val destination = mutableListOf<NoteResponse>()

        val transform: suspend (value: Notes) -> NoteResponse = {
            it.toResponse()!!
        }
        val notes = repo.findAll().map(transform).toList(destination)
        return ServerResponse.ok().bodyValue(Response.success("", notes)).awaitSingle()
    }
}