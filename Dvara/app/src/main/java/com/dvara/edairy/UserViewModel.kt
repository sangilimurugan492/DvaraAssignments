package com.dvara.edairy

import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dvara.edairy.Data.entity.User
import kotlinx.coroutines.*

class UserViewModel(private val repository: UserRepository) : ViewModel() {


    fun saveUserData(user: User) {
        insertUser(user)
    }

    fun fetchUserData() {

    }

    fun insertUser(user: User) =
        viewModelScope.launch {
            repository.insertUser(user)
        }

    fun updateUser(user: User) =
        viewModelScope.launch {
            repository.updateUser(user)

    }
}