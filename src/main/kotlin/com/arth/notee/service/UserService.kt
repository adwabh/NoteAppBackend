package com.arth.notee.service

import com.arth.notee.network.Response
import com.arth.notee.repository.UserRepository
import com.arth.notee.repository.Users
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(@Autowired val repo: UserRepository) {

    @Operation(
        method = "GET",
        summary = "get all users",
        description = "get all users",
        responses = [ApiResponse(responseCode = "200", description = "get all users")],
        requestBody = RequestBody(description = "Note id", required = true)
    )
    suspend fun getAllUsers(): Response<List<Users>?> {
        val body = repo.findAll().toList()
        return Response.success("S200", body)
    }

    @Operation(
        method = "GET",
        summary = "get users by Id",
        description = "get users by Id",
        responses = [ApiResponse(responseCode = "200", description = "get users by Id"), ApiResponse(
            responseCode = "404", description = "User not found"
        )]
    )
    suspend fun getUserById(id: Int): Response<out Users> {
        val body = repo.findById(id) ?: return Response.failure("E404", "user not found")
        return Response.success("S200", body)
    }

    @Operation(
        method = "POST",
        summary = "Create user",
        description = "Create user",
        responses = [ApiResponse(responseCode = "200", description = "user created")]
    )
    suspend fun createUser(user: Users): Users? {
        return repo.save(user)
    }

    @Operation(
        method = "PUT",
        summary = "update user",
        description = "Create user",
        responses = [ApiResponse(responseCode = "200", description = "user created"), ApiResponse(
                responseCode = "404", description = "User not found"
    )],
        requestBody = RequestBody(description = "User id", required = true)
    )
    suspend fun updateUser(user: Users): Response<out Users> {
        if (user.userid == null) {
            return Response.failure("E400", "id is required")
        }
        if (repo.findById(user.userid) == null) {
            return Response.failure("E404", "user not found")
        }
        return Response.success("S200", repo.save(user))
    }

    @Operation(
        method = "DELETE",
        summary = "Delete user record",
        description = "Delete user record",
        responses = [ApiResponse(responseCode = "200", description = "User deleted"), ApiResponse(
            responseCode = "404", description = "User not found"
        )],
        requestBody = RequestBody(description = "User id", required = true)
    )
    suspend fun deleteUser(id: Int): Response<out Users> {
        val user = repo.findById(id) ?: return Response.failure("E404", "user not found")
        repo.delete(user)
        return Response.success("S200", user)
    }
}
