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
}
