package com.arth.notee.repository

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.sql.Timestamp
import java.time.OffsetDateTime

interface NotesCoRoutinesRepository : CoroutineCrudRepository<Notes, String>

data class Notes(
    @Id val id: Int? = null,
    val title: String,
    val body: String,
    val createdDate: OffsetDateTime,
    val updatedDate: OffsetDateTime,
)

