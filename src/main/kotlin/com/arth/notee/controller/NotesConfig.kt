package com.arth.notee.controller

import com.arth.notee.service.NoteService
import com.arth.notee.service.UserService
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter


// https://github.com/springdoc/springdoc-openapi-demos/tree/2.x refer this and implement

@Configuration
class NotesConfig {

    @Bean
    fun notesOpenApi(@Value("\${springdoc.version}") appVersion: String?): GroupedOpenApi {
        val paths = arrayOf("/v1/**")
        return GroupedOpenApi.builder().group("notes")
            .pathsToMatch(*paths)
            .packagesToScan("com.arth.notee.controller")
            .addOpenApiCustomizer { openApi ->
                openApi.info(
                    io.swagger.v3.oas.models.info.Info().title("Notes API")
                        .version(appVersion)
                        .description("Notes Information")
                )
            }
            .build()
    }
    @Bean
    @RouterOperations(
        *arrayOf(
            RouterOperation(path = "/v1/note", method = arrayOf(RequestMethod.GET), beanClass = NoteService::class, beanMethod = "getAllNotes"),
            RouterOperation(path = "/v1/note/{id}", method = arrayOf(RequestMethod.GET), beanClass = NoteService::class, beanMethod = "getNoteById"),
            RouterOperation(path = "/v1/note", method = arrayOf(RequestMethod.POST), beanClass = NoteService::class, beanMethod = "createNote"),
            RouterOperation(path = "/v1/note/{id}", method = arrayOf(RequestMethod.PUT), beanClass = NoteService::class, beanMethod = "updateNote"),
            RouterOperation(path = "/v1/note/{id}", method = arrayOf(RequestMethod.DELETE), beanClass = NoteService::class, beanMethod = "deleteNote")
        )
    )
    fun notes(@Autowired noteService: NoteService): RouterFunction<ServerResponse> {
        return coRouter {

            ("/v1").nest {

                getAllNotes(noteService)

                getNoteById(noteService)

                createNote(noteService)

                updateNote(noteService)

                deleteNote(noteService)
            }
        }
    }

    private fun CoRouterFunctionDsl.deleteNote(service: NoteService) = DELETE("/note/{id}") {
        service.deleteNote(it)
    }

    private fun CoRouterFunctionDsl.updateNote(service: NoteService) = PUT("/note/{id}") {
        service.updateNote(it)
    }

    private fun CoRouterFunctionDsl.createNote(service: NoteService) = POST("/note") {
        service.createNote(it)
    }
    private fun CoRouterFunctionDsl.getNoteById(service: NoteService) = GET("/note/{id}") {
        service.getNoteById(it)
    }
    private fun CoRouterFunctionDsl.getAllNotes(service: NoteService) = GET("/notes") { it ->
        service.getAllNotes(it)
    }


}
