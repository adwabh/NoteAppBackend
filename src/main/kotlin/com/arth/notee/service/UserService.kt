package com.arth.notee.service

import com.arth.notee.network.Response
import com.arth.notee.repository.UserRepository
import com.arth.notee.repository.Users
import kotlinx.coroutines.flow.firstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*

@Service
class UserService(@Autowired val repo: UserRepository) {

    suspend fun getAllUsers(request: ServerRequest):ServerResponse {
        val body = repo.findAll()
        val res = Response.success("S200", body)
        return ServerResponse.ok().bodyValueAndAwait(res)
    }

    suspend fun getUserById(id: Int): Response<Users?> {
        val body = repo.findById(id)
        return Response.success("S200", body)
    }

    suspend fun createUser(request: ServerRequest): ServerResponse {

        return request
            .bodyToFlow<Users>()
            .firstOrNull()?.let {
                val body = repo.save(it)
                val res = Response.success("S200", body)
                ServerResponse.ok().bodyValueAndAwait(res)
            }
            ?: run {
                val res = Response.failure("E400", "body is required")
                ServerResponse.badRequest().bodyValueAndAwait(res)
            }
    }

    suspend fun updateUser(request: ServerRequest): ServerResponse {
        val id = request.headers()
            .header("id")
            .firstOrNull()
            ?.toInt()
        if (id == null) {
            val res = Response.failure("E400", "id is required")
            return ServerResponse.badRequest().bodyValueAndAwait(res)
        }
        return request.headers().header("id").firstOrNull()?.let {
            val user = repo.findById(id)
            user?.let {
                repo.save(user)
                val res = Response.success("S200", user)
                ServerResponse.ok().bodyValueAndAwait(res)
            }?: run {
                val res = Response.failure("E404", "user not found")
                ServerResponse.badRequest().bodyValueAndAwait(res)
            }

        } ?: run {
            val res = Response.failure("E400", "body is required")
            ServerResponse.badRequest().bodyValueAndAwait(res)
        }
    }

    suspend fun deleteUser(request: ServerRequest): ServerResponse {
        val id = request.headers()
            .header("id")
            .firstOrNull()
            ?.toInt()
        if (id == null) {
            val res = Response.failure("E400", "id is required")
            return ServerResponse.badRequest().bodyValueAndAwait(res)
        }
        return request.headers().header("id").firstOrNull()?.let {
            val user = repo.findById(id)
            user?.let {
                repo.delete(user)
                val res = Response.success("S200", user)
                ServerResponse.ok().bodyValueAndAwait(res)
            } ?: run {
                val res = Response.failure("E404", "user not found")
                ServerResponse.badRequest().bodyValueAndAwait(res)
            }

        } ?: run {
            val res = Response.failure("E400", "body is required")
            ServerResponse.badRequest().bodyValueAndAwait(res)
        }
    }
}
