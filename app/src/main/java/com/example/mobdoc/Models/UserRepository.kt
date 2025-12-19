package com.example.mobdoc.Models

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class UserRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")
    // Create
    suspend fun addUser(user: User): String {
        val id = usersRef.push().key ?: UUID.randomUUID().toString()
        val userWithId = user.copy(
            name = user.name,
        )
        usersRef.child(id).setValue(userWithId).await()
        return id
    }

    // Read - все задачи
    suspend fun getAllUsers(): List<User> {
        return usersRef.get().await().children.map { snapshot ->
            snapshot.getValue(User::class.java) ?: User()
        }
    }

    // Read - по ID
    suspend fun getUserById(id: String): User? {
        return usersRef.child(id).get().await().getValue(User::class.java)
    }

    // Update
    suspend fun updateUser(user: User) {
        val updatedUser = user.copy()
        usersRef.child(user.name).setValue(updatedUser).await()
    }

    // Delete
    suspend fun deleteUser(userId: String) {
        usersRef.child(userId).removeValue().await()
    }

    // LiveData для наблюдения за изменениями
    fun observeUsers(): LiveData<List<User>> {
        return object : LiveData<List<User>>() {
            private val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                    postValue(users)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обработка ошибки
                }
            }

            override fun onActive() {
                usersRef.addValueEventListener(valueEventListener)
            }

            override fun onInactive() {
                usersRef.removeEventListener(valueEventListener)
            }
        }
    }
}