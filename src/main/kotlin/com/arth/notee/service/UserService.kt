package com.arth.notee.service

import com.arth.notee.network.Response
import com.arth.notee.repository.UserRepository
import com.arth.notee.repository.Users
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(@Autowired val repo: UserRepository) {

    suspend fun getAllUsers(): Response<List<Users>?> {
        val body = repo.findAll().toList()
        return Response.success("S200", body)
    }

    suspend fun getUserById(id: Int): Response<out Users> {
        val body = repo.findById(id) ?: return Response.failure("E404", "user not found")
        return Response.success("S200", body)
    }

    suspend fun createUser(user: Users): Users? {
        return repo.save(user)
    }

    suspend fun updateUser(user: Users): Response<out Users> {
        if (user.userid == null) {
            return Response.failure("E400", "id is required")
        }
        if (repo.findById(user.userid) == null) {
            return Response.failure("E404", "user not found")
        }
        return Response.success("S200", repo.save(user))
    }
    /*suspend fun updateUser(request: Users): ServerResponse {
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
    }*/

    suspend fun deleteUser(id: Int): Response<out Users> {
        val user = repo.findById(id) ?: return Response.failure("E404", "user not found")
        repo.delete(user)
        return Response.success("S200", user)
    }
}
