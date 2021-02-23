package com.dvara.edairy

import androidx.lifecycle.LiveData
import com.dvara.edairy.Data.dao.UserDao
import com.dvara.edairy.Data.entity.User

class UserRepository(private val dao : UserDao) {

    suspend fun getUserByMobile(mobileNo : String) : LiveData<User> {
        return dao.getUserByMobile(mobileNo)
    }

    suspend fun insertUser(user : User) {
        dao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        dao.updateUser(user)
    }
}