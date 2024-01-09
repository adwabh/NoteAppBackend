package com.arth.notee.service

import com.arth.notee.controller.UsersConfig
import com.arth.notee.network.Response
import com.arth.notee.repository.UserRepository
import com.arth.notee.repository.Users
import com.ninjasquad.springmockk.MockkBean
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import org.springframework.test.context.junit.jupiter.SpringExtension

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
        userConfig.users(userService)
    }
    @AfterEach
    fun tearDown() {
    }

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
        runBlocking {
            // Assign
            val user = Users(username = "user1", email = "user1@domain.com")
            coEvery { userRepository.save(user) } returns user
            // Act
            val actual = userService.createUser(user)
            // Assert
            assert(user == actual)
        }

    }

    @Test
    fun `updateUser should update user`() {
        runBlocking {
            // Assign
            val users = Users(1, "user1", email = "user1@domain.com")
            val expected = Response.success("S200", users)
            coEvery { userRepository.findById(1) } returns users
            coEvery { userRepository.save(users) } returns users
            // Act
            val actual = userService.updateUser(users)
            // Assert
            assert(expected == actual)
        }
    }

    @Test
    fun `deleteUser should delete user`() {
        runBlocking {
            // Assign
            val users = Users(1, "user1", email = "user1@domain.com")
            coEvery { userRepository.findById(1) } returns users
            coEvery { userRepository.delete(users) } returns Unit
            // Act
            assertDoesNotThrow { userService.deleteUser(users.userid!!) }
        }
    }

    @Test
    fun `deleteUser should return 404 if user not found`() {
        // Assign
        runBlocking {
            val user = Users(1, "user1", email = "user1@domain.com")
            coEvery { userRepository.findById(1) } returns null
            coEvery { userRepository.delete(user) } returns Unit
            // Act
            val res = userService.deleteUser(user.userid!!)
            // Assert
            assertAll(
                { assert(res.success == false) },
                { assert(res.code == "E404") },
                { assert(res.error?.message == "user not found") },
                { assert(res.body == null) }
            )
        }
    }

    @Test
    fun `updateUser should return 404 if user not found`() {
        // Assign
        runBlocking {
            val user = Users(1, "user1", email = "user1@domain.com")
            coEvery { userRepository.findById(1) } returns null
            coEvery { userRepository.save(user) } returns user
            // Act
            val res = userService.updateUser(user)
            // Assert
            assertAll(
                { assert(res.success == false) },
                { assert(res.code == "E404") },
                { assert(res.error?.message == "user not found") },
                { assert(res.body == null) }
            )
        }
    }

    @Test
    fun `getUserById should return 404 if user not found`() {
        runBlocking {
            // Assign
            val user = Users(1, "user1", email = "")
            coEvery { userRepository.findById(1) } returns null
            coEvery { userRepository.save(user) } returns user
            // Act
            val res = userService.getUserById(user.userid!!)
            // Assert
            assertAll(
                { assert(res.success == false) },
                { assert(res.code == "E404") },
                { assert(res.error?.message == "user not found") },
                { assert(res.body == null) }
            )
        }
    }

    @Test
    fun `getAllUsers should return empty list if no users found`() {
        runBlocking {
            // Assign
            coEvery { userRepository.findAll() } returns flowOf()
            // Act
            val res = userService.getAllUsers()
            // Assert
            assertAll(
                { assert(res.success) },
                { assert(res.code == "S200") },
                { assert(res.error == null) },
                { assert(res.body!!.isEmpty()) }
            )
    }
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
        MockServerRequest.builder().build()
        userService.getAllUsers()
        //Assert
        coVerify(atLeast = 1) { userRepository.findAll() }
    }

    @Test
    fun `getUserById should call repository findById`() = runBlocking {
        //Assign
        val id = 1

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

        coEvery { userRepository.save(user) } returns user
        //Act
        userService.createUser(user)
        //Assert
        coVerify {
            userRepository.save(user)
        }
    }

    @Test
    fun `updateUser should call repository save`() = runBlocking {
        //Assign
        val user = Users(1, "user1", "user1@domain.com")
        coEvery { userRepository.findById(1) } returns user
        coEvery { userRepository.save(user) } returns user
        //Act
        userService.updateUser(user)
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
        userService.deleteUser(user.userid!!)
        //Assert
        coVerify {
            userRepository.delete(user)
        }
    }
}