package com.dvara.edairy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvara.edairy.Data.entity.User
import kotlinx.coroutines.*

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun saveUserData(user: User) {
        insertUser(user)
    }

    private fun insertUser(user: User) =
        viewModelScope.launch {
            repository.insertUser(user)
        }

}