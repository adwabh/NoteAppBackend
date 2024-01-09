package com.arth.notee.controller

import com.arth.notee.network.Response
import com.arth.notee.repository.Users
import com.arth.notee.service.UserService
import kotlinx.coroutines.flow.firstOrNull
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.*

@Configuration
class UsersConfig {
    @Bean
    @RouterOperations(
        RouterOperation()
    )
    fun users(@Autowired userService: UserService): RouterFunction<ServerResponse> {
        return coRouter {

            ("/v1").nest {

                getAllUsers(userService)

                getUserById(userService)

                createUser(userService)

                updateUser(userService)

                deleteUser(userService)
            }
        }
    }

    private fun CoRouterFunctionDsl.deleteUser(userService: UserService) = DELETE("/user/{id}") { request ->
        val id = request.pathVariable("id")
        id.let {
            val body = userService.deleteUser(it.toInt())
            bodyBuilder(body)
        }
    }

    private fun CoRouterFunctionDsl.updateUser(userService: UserService) = PUT("/user") { request ->
        request
            .bodyToFlow<Users>()
            .firstOrNull()?.let {
                val body = userService.updateUser(it)
                bodyBuilder(body)
            }
            ?: run {
                val res = Response.failure("E400", "body is required")
                ServerResponse.badRequest().bodyValueAndAwait(res)
            }
    }

    private fun CoRouterFunctionDsl.createUser(userService: UserService): Unit = POST("/user") { request ->
        request
            .bodyToFlow<Users>()
            .firstOrNull()?.let {
                val body = userService.createUser(it)
                val res = Response.success("S200", body)
                ServerResponse.ok().bodyValueAndAwait(res)
            }
            ?: run {
                val res = Response.failure("E400", "body is required")
                ServerResponse.badRequest().bodyValueAndAwait(res)
            }
    }

    private fun CoRouterFunctionDsl.getUserById(userService: UserService) = GET("/user/{id}") { request ->
        with(request.pathVariable("id")) {
            val body = userService.getUserById(toInt())
            bodyBuilder(body)
        }
    }

    private suspend fun bodyBuilder(body: Response<out Users>) =
        if (body.success) {
            ServerResponse.ok()
        } else {
            ServerResponse.badRequest()
        }.bodyValueAndAwait(body)

    private fun CoRouterFunctionDsl.getAllUsers(userService: UserService) = GET("/user") {
        val res = userService.getAllUsers()
        ServerResponse.ok().bodyValueAndAwait(res)
    }
}