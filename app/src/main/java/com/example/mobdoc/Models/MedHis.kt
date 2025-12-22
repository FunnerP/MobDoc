package com.example.mobdoc.Models

import android.icu.util.Calendar
import com.google.firebase.Timestamp

data class MedHis(
    val date: Timestamp = Timestamp.now(),
    val history: String,
    val patientId: String
)
