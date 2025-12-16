package com.example.mobdoc.ViewModels

import androidx.compose.ui.semantics.Role
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.mobdoc.Models.User
import com.example.mobdoc.Models.UserRepository
import com.google.android.datatransport.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel: ViewModel() {
    private val repository = UserRepository()
    private val _users = MutableLiveData<List<User>>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _errorMessage = MutableLiveData<String?>()
    private val currentUser = MutableStateFlow<User?>(null)

    val users: LiveData<List<User>> = _users
    val isLoading: LiveData<Boolean> = _isLoading
    val errorMessage: LiveData<String?> = _errorMessage
    val user: StateFlow<User?> = currentUser.asStateFlow()

    init {
        loadUsers()
        observeUsers()
    }
    fun getUser(id:String){
        viewModelScope.launch {
            currentUser.value= repository.getUserById(id)!!
        }
    }
    private fun observeUsers() {
        repository.observeUsers().observeForever { usersList ->
            _users.value = usersList
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _users.value = repository.getAllUsers()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки пользователей: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun addUser(name: String, password: String, role: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newUser = User(
                    name = name,
                    password = password,
                    role = role
                )
                repository.addUser(newUser)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка обновления пользователя: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                repository.updateUser(user)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка обновления пользователя: ${e.message}"
            }
        }
    }
    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                repository.deleteUser(userId)
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка удаления пользователя: ${e.message}"
            }
        }
    }
    fun clearError() {
        _errorMessage.value = null
    }
}