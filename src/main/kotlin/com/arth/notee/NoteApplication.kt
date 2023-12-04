package com.arth.notee

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<NoteApplication>(*args)
}

@SpringBootApplication
class NoteApplication
