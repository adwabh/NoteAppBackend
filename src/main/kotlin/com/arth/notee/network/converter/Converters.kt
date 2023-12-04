package com.arth.notee.network.converter

import com.arth.notee.network.NoteResponse
import com.arth.notee.repository.Notes

object Converters {
    fun Notes?.toResponse(): NoteResponse? {
        return this?.let {
            NoteResponse(
                it.id?.toString() ?: "", it.title, it.body, it.createdDate.toEpochSecond(), it.updatedDate.toEpochSecond()
            )
        }
    }
}