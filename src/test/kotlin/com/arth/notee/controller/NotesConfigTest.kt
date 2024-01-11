package com.arth.notee.controller

import com.arth.notee.repository.Notes
import com.arth.notee.repository.NotesCoRoutinesRepository
import com.arth.notee.service.NoteService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.OffsetDateTime

@ExtendWith(SpringExtension::class)
class NotesConfigTest {
    private lateinit var client: WebTestClient
    private lateinit var noteService: NoteService
    private lateinit var notesConfig: NotesConfig
    @MockkBean
    private lateinit var notesRepository: NotesCoRoutinesRepository

    @BeforeEach
    fun setUp() {
        notesConfig = NotesConfig()
        noteService = NoteService(notesRepository)
        val notesRoute = notesConfig.notes(noteService)
        client = WebTestClient.bindToRouterFunction(notesRoute).build()
    }

    @Test
    fun `notes should return all notes`() {
        // Assign
        val notes = Notes(1, "note1", "note1 content", OffsetDateTime.now(), OffsetDateTime.now())
        coEvery { notesRepository.findAll() } returns flowOf(notes)
        // Assert
        // Act
        client.get()
            .uri("/v1/notes")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should return note by id`() {
        // Assign
        val notes = Notes(1, "note1", "note1 content", OffsetDateTime.now(), OffsetDateTime.now())
        coEvery { notesRepository.findById("1") } returns notes
        // Act // Assert
        client.get()
            .uri("/v1/note/1")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should create note`() {
        // Assign
        val notes = Notes(1, "note1", "note1 content", OffsetDateTime.now(), OffsetDateTime.now())
        coEvery { notesRepository.save(notes) } returns notes
        // Act // Assert
        client.post()
            .uri("/v1/note")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should update note`() {
        // Assign
        val notes = Notes(1, "note1", "note1 content", OffsetDateTime.now(), OffsetDateTime.now())
        coEvery { notesRepository.save(notes) } returns notes
        // Act // Assert
        client.put()
            .uri("/v1/note/1")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should delete note`() {
        // Assign
        val notes = Notes(1, "note1", "note1 content", OffsetDateTime.now(), OffsetDateTime.now())
        coEvery { notesRepository.save(notes) } returns notes
        // Act // Assert
        client.delete()
            .uri("/v1/note/1")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should return error 404 if note not found`() {
        // Assign
        coEvery { notesRepository.findById("2") } returns null
        // Act // Assert
        client.get()
            .uri("/v1/note/2")
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should return error 400 if note id is not provided`() {
        // Assign
        coEvery { notesRepository.findById("2") } returns null
        // Act // Assert
        client.get()
            .uri("/v1/note/")
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should return error 400 if note body is not provided`() {
        // Assign
        coEvery { notesRepository.findById("2") } returns null
        // Act // Assert
        client.get()
            .uri("/v1/note/")
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should return error 400 if note title is not provided`() {
        // Assign
        coEvery { notesRepository.findById("2") } returns null
        // Act // Assert
        client.get()
            .uri("/v1/note/")
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should return error 400 if note created date is not provided`() {
        // Assign
        coEvery { notesRepository.findById("2") } returns null
        // Act // Assert
        client.get()
            .uri("/v1/note/")
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should return error 400 if note updated date is not provided`() {
        // Assign
        coEvery { notesRepository.findById("2") } returns null
        // Act // Assert
        client.get()
            .uri("/v1/note/")
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should return error 400 if note id is not valid`() {
        // Assign
        coEvery { notesRepository.findById("2") } returns null
        // Act // Assert
        client.get()
            .uri("/v1/note/")
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should return error 400 if note body is not valid`() {
        // Assign
        coEvery { notesRepository.findById("2") } returns null
        // Act // Assert
        client.get()
            .uri("/v1/note/")
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `notes should return error 400 if note title is not valid`() {
        // Assign
        coEvery { notesRepository.findById("2") } returns null
        // Act // Assert
        client.get()
            .uri("/v1/note/")
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult().responseBody.toString()
    }
}
