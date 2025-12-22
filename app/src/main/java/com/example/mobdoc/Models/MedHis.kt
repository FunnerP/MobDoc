package com.example.mobdoc.Models

import com.google.firebase.Timestamp

data class MedHis(
    val id: String = "",
    val date: Timestamp = Timestamp.now(),
    val history: String,
    val patientId: String
)
