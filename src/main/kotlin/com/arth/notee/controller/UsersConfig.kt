package com.arth.notee.controller

import com.arth.notee.network.Response
import com.arth.notee.repository.Users
import com.arth.notee.service.UserService
import kotlinx.coroutines.flow.firstOrNull
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.reactive.function.server.*

@Configuration
class UsersConfig {

    @Bean
    fun usersOpenApi(@Value("\${springdoc.version}") appVersion: String?): GroupedOpenApi {
        val paths = arrayOf("/v1/user/**")
        return GroupedOpenApi.builder().group("user")
            .pathsToMatch(*paths)
            .packagesToScan("com.arth.notee.controller")
            .addOpenApiCustomizer { openApi ->
                openApi.info(
                    io.swagger.v3.oas.models.info.Info().title("User API")
                        .version(appVersion)
                        .description("User API Information")
                )
            }
            .build()
    }
    @Bean
    @RouterOperations(
        *arrayOf(
            RouterOperation(path = "/v1/user", method = arrayOf(RequestMethod.GET), beanClass = UserService::class, beanMethod = "getAllUsers"),
            RouterOperation(path = "/v1/user/{id}", method = arrayOf(RequestMethod.GET), beanClass = UserService::class, beanMethod = "getUserById"),
            RouterOperation(path = "/v1/user", method = arrayOf(RequestMethod.POST), beanClass = UserService::class, beanMethod = "createUser"),
            RouterOperation(path = "/v1/user/{id}", method = arrayOf(RequestMethod.PUT), beanClass = UserService::class, beanMethod = "updateUser"),
            RouterOperation(path = "/v1/user/{id}", method = arrayOf(RequestMethod.DELETE), beanClass = UserService::class, beanMethod = "deleteUser")
        )
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