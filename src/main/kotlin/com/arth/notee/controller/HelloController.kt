package com.arth.notee.controller

import com.arth.notee.NoteApplication
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping("/hello-controller")
    suspend fun sayHello(): NoteApplication.Response {
        return NoteApplication.Response(
            "hello",
            HttpStatus.OK.value().toString(),
            success = true
        )
    }
}