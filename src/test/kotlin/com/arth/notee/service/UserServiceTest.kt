package com.arth.notee.service

import com.arth.notee.controller.UsersConfig
import com.arth.notee.network.Response
import com.arth.notee.repository.UserRepository
import com.arth.notee.repository.Users
import com.ninjasquad.springmockk.MockkBean
import io.mockk.InternalPlatformDsl.toStr
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.MediaType
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

@ExtendWith(SpringExtension::class)
class UserServiceTest {

    private val userConfig: UsersConfig by lazy { UsersConfig() }

    private lateinit var userService: UserService

//    private lateinit var client: WebTestClient

    @MockkBean
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        userService = UserService(userRepository)
        val usersRoute = userConfig.users(userService)
//        client = WebTestClient.bindToRouterFunction(usersRoute).build()
    }
    @AfterEach
    fun tearDown() {
    }

    /*@Test
    fun `getAllUsers should return all users`() {
        //Assign
        val users = Users(1, "user1", email = "user1@domain.com")
        coEvery { userRepository.findAll() } returns flowOf(users)
        //Act //Assert
       val str =  client.get()
            .uri("v1/user")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .returnResult().mockServerResult.toStr()
            println(str)
    }*/

    @Test
    fun `getUserById should return user with given id`() = runBlocking {
        //Assign
        val userid = 1
        val users = Users(userid, "user1", email = "user1@domain.com")
        coEvery { userRepository.findById(1) } returns users
        val expected = Response.success("S200", users)

        //Act
        val actual = userService.getUserById(userid)
        //Assert
        assert(expected == actual)
    }

    @Test
    fun `createUser should create user`() {

    }

    @Test
    fun `updateUser should update user`() {
    }

    @Test
    fun `deleteUser should delete user`() {
    }

    @Test
    fun `deleteUser should return 404 if user not found`() {
    }

    @Test
    fun `deleteUser should return 500 if error occurs`() {
    }

    @Test
    fun `updateUser should return 404 if user not found`() {
    }

    @Test
    fun `updateUser should return 500 if error occurs`() {
    }

    @Test
    fun `createUser should return 500 if error occurs`() {
    }

    @Test
    fun `getUserById should return 404 if user not found`() {
    }
    @Test
    fun `getUserById should return 500 if error occurs`() {
    }

    @Test
    fun `getAllUsers should return 500 if error occurs`() {
    }

    @Test
    fun `getAllUsers should return 404 if no users found`() {
    }

    @Test
    fun `getAllUsers should return 200 if users found`() {

    }

    @Test
    fun `getAllUsers should call repository findAll`(): Unit = runBlocking {
        //Assign
        val user1 = Users(1, "user1", "user1@domain.com")
        every { userRepository.findAll() } returns flowOf(user1)
        //Act
        val request = MockServerRequest.builder().build()
        userService.getAllUsers(request)
        //Assert
        coVerify(atLeast = 1) { userRepository.findAll() }
    }

    @Test
    fun `getUserById should call repository findById`() = runBlocking {
        //Assign
        val id = 1
        val request = MockServerRequest.builder()
            .header("id", id.toString())
            .build()
        coEvery { userRepository.findById(id) } returns Users(id, "user1", "user1@domain.com")
        //Act
        userService.getUserById(id)
        //Assert
        coVerify { userRepository.findById(id) }
    }

    @Test
    fun `createUser should call repository save`() = runBlocking {
        //Assign
        val user = Users(username = "user1", email = "user1@domain.com")
        val request = MockServerRequest.builder()
            .uri(URI.create("/note"))
            .body(Flux.from(Mono.just(user)))

        coEvery { userRepository.save(user) } returns user
        //Act
        userService.createUser(request)
        //Assert
        coVerify {
            userRepository.save(user)
        }
    }

    @Test
    fun `updateUser should call repository save`() = runBlocking {
        //Assign
        val request = MockServerRequest.builder()
            .header("id", "1")
            .body(Flux.from(Mono.empty<Users>()))
        val user = Users(1, "user1", "user1@domain.com")
        coEvery { userRepository.findById(1) } returns user
        coEvery { userRepository.save(user) } returns user
        //Act
        userService.updateUser(request)
        //Assert
        coVerify { userRepository.save(any()) }
    }

    @Test
    fun `deleteUser should call repository delete`() = runBlocking {
        //Assign
        val user = Users(1, "user1", "user1@domain.com")
        coEvery { userRepository.findById(1) } returns user
        coEvery { userRepository.delete(user) } returns Unit
        //Act
        val request = MockServerRequest.builder().header("id", "1").build()
        userService.deleteUser(request)
        //Assert
        coVerify {
            userRepository.delete(user)
        }
    }
}