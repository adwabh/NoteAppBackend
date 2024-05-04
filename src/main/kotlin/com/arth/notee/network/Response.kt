package com.arth.notee.network

data class Response<T>(
    val success: Boolean,
    val code: String,
    val data: T?,
    val error: ErrorResponse?

) {
    companion object {
        fun <T> success(code: String, body: T): Response<T> {
            return Response(true, code, data = body, error = null)
        }

        fun failure(code: String, message: String): Response<Nothing> {
            val error = ErrorResponse(code, message = message)
            return Response(false, code, null, error)
        }
    }
}

data class ErrorResponse(
    val code: String,
    val message: String
)
data class NotesListResponse(
    val notes: List<NoteResponse>
)

data class NoteResponse(
    val id: String,
    val title: String,
    val body: String,
    val createdDate: Long,
    val updatedDate: Long?
)
