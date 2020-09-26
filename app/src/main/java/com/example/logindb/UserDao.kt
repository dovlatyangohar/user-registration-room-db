package com.example.logindb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table")
    suspend fun getAllUsers(): List<User>

    @Query("SELECT * FROM user_table where login is :currentLogin")
    suspend fun loadLoggedUser(currentLogin: String): User?

    @Insert
    suspend fun insertUser(user: User): Long

    @Insert
    suspend fun insertAll(vararg user: User)

    @Delete
    suspend fun delete(user: User)
}