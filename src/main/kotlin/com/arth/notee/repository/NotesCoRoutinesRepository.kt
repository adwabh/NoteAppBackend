package com.arth.notee.repository

import org.springframework.data.annotation.Id
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface NotesCoRoutinesRepository : CoroutineCrudRepository<NotesEntity, String>
data class NotesEntity(
    @Id val id: String,
    val title: String,
    val body: String,
    val createdDate: Long,
    val updatedDate: Long
)
