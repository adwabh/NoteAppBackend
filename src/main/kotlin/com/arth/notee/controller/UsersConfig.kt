package com.arth.notee.controller

import com.arth.notee.service.UserService
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

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

                /*getUserById(userService)

                createUser(userService)

                updateUser(userService)

                deleteUser(userService)*/
            }
        }
    }

    private fun CoRouterFunctionDsl.deleteUser(userService: UserService): ServerResponse {
        TODO("Not yet implemented")
    }

    private fun CoRouterFunctionDsl.updateUser(userService: UserService): ServerResponse {
        TODO("Not yet implemented")
    }

    private fun CoRouterFunctionDsl.createUser(userService: UserService): ServerResponse {
        TODO("Not yet implemented")
    }

    private fun CoRouterFunctionDsl.getUserById(userService: UserService): ServerResponse {
        TODO("Not yet implemented")
    }

    private fun CoRouterFunctionDsl.getAllUsers(userService: UserService) = GET("/user") {
        userService.getAllUsers(it)
    }
}