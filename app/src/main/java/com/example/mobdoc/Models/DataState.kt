package com.example.mobdoc.Models

sealed class DataState {
    class Success(val data: MutableList<User>): DataState()
    class Failure(val message: String): DataState()
    object Loading: DataState()
    object Empty: DataState()
}