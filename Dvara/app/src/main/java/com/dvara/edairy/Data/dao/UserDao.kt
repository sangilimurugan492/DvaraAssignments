package com.dvara.edairy.Data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import com.dvara.edairy.Data.entity.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Insert(onConflict = REPLACE)
    suspend fun insertUser(user : User)

    @Update
    suspend fun updateUser(user : User)

    @Query("SELECT * FROM User WHERE mobile = :mobile")
    suspend fun getUserByMobile(mobile: String) : LiveData<User>

    @Delete
    suspend fun delete(user: User)
}