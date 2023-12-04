package com.arth.notee.exception

import com.arth.notee.network.ErrorResponse
import com.arth.notee.network.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

const val ERROR_GENERIC = "E402"
const val MESSAGE_GENERIC = "Something went wrong"

@RestControllerAdvice
class GlobalExceptionHandler {

    //TODO: handle specific classes of exceptions
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleException(): ResponseEntity<Response<Nothing>> {
        val error = ErrorResponse(ERROR_GENERIC, MESSAGE_GENERIC)
        return ResponseEntity<Response<Nothing>>(
            Response(
                false, code = ERROR_GENERIC, null, error
            ), HttpStatus.NOT_FOUND
        )
    }
}