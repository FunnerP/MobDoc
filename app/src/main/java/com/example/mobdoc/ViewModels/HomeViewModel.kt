package com.example.mobdoc.ViewModels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mobdoc.Models.DataState
import com.example.mobdoc.Models.User

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class HomeViewModel: ViewModel() {
    val response: MutableState<DataState> = mutableStateOf(DataState.Empty)
    init {
        fetchDataFromFirebase()
    }
    private fun fetchDataFromFirebase(){
        val tempList=mutableListOf<User>()
        response.value= DataState.Loading
        FirebaseDatabase.getInstance().getReference("Category").
        addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnap in snapshot.children){
                    val foodItem=dataSnap.getValue(User::class.java)
                    if(foodItem!=null) tempList.add(foodItem)
                }
                response.value= DataState.Success(tempList)
            }
            override fun onCancelled(error: DatabaseError) {
                response.value= DataState.Failure(error.message)
            }
        }
        )
    }
}