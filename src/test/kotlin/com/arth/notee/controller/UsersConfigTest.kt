package com.arth.notee.controller

import com.arth.notee.network.Response
import com.arth.notee.repository.UserRepository
import com.arth.notee.repository.Users
import com.arth.notee.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
class UsersConfigTest {

    private lateinit var objectMapper: ObjectMapper
    private lateinit var client: WebTestClient
    private lateinit var userService: UserService
    private lateinit var usersConfig: UsersConfig
    @MockkBean
    private lateinit var userRepository: UserRepository
    @BeforeEach
    fun setUp() {
        usersConfig = UsersConfig()
        userService = UserService(userRepository)
        val usersRoute = usersConfig.users(userService)
        client = WebTestClient.bindToRouterFunction(usersRoute).build()
        objectMapper = jacksonObjectMapper()
    }
    @Test
    fun `users should return all users`() {
        //Assign
        val users = Users(1, "user1", email = "user1@domain.com")
        coEvery { userRepository.findAll() } returns flowOf(users)
        //Act //Assert
        client.get()
            .uri("/v1/user")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `users should return user by id`() {
        //Assign
        val users = Users(1, "user1", email = "")
        coEvery { userRepository.findById(1) } returns users
        //Act //Assert
        client.get()
            .uri("/v1/user/1")
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `user by id should return error 404 if user not found`() {
        //Assign
        coEvery { userRepository.findById(2) } returns null
        //Act //Assert
        client.get()
            .uri("/v1/user/2")
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `create user should create the user record and return created user data`() {
        //Assign
        val users = Users(1, "user1", email = "")
        coEvery { userRepository.save(users) } returns users
        //Act //Assert
        client.post()
            .uri("/v1/user")
            .bodyValue(users)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .returnResult().responseBody.toString()
    }

    @Test
    fun `update user should update the user record and return updated user data`() {
        //Assign
        val user = Users(1, "user1", email = "")
        val usersUpdate = Users(1, "user1", email = "user1@domain.com")
        coEvery { userRepository.save(usersUpdate) } returns usersUpdate
        coEvery { userRepository.findById(user.userid!!) } returns user
        val expected = Response.success("S200", usersUpdate)
        val expectedJson = objectMapper.writeValueAsString(expected)
        //Act //Assert
        client.put()
            .uri("/v1/user")
            .bodyValue(usersUpdate)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .json(expectedJson)
    }

    @Test
    fun `update user should return error 404 if user not found`() {
        //Assign
        val user = Users(1, "user1", email = "")
        val usersUpdate = Users(1, "user1", email = "user1@domain.com")
        coEvery { userRepository.findById(user.userid!!) } returns null
        //Act //Assert
        client.put()
            .uri("/v1/user")
            .bodyValue(usersUpdate)
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult().responseBody.toString()
    }
}