package com.arth.notee.repository

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<Users, Int>

data class Users(val userid: Int? = null, val username:String, val email: String)
