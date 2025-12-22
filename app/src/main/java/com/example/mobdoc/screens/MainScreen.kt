package com.example.mobdoc.screens

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobdoc.AppNavHost
import com.example.mobdoc.Models.Doctor
import com.example.mobdoc.Models.MedHis
import com.example.mobdoc.Models.Patient
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

@Composable
fun MainScreen(navController: NavController) {
    val firestore = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

    val currentUser = auth.currentUser ?: return

    var userRole by remember { mutableStateOf<String?>(null) }
    var doctors by remember { mutableStateOf(listOf<Doctor>()) }
    var patients by remember { mutableStateOf(listOf<Patient>()) }
    val medhists by remember { mutableStateOf(listOf<MedHis>()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedPatient by remember { mutableStateOf<Patient?>(null) }

    LaunchedEffect(currentUser.uid) {
        // Определяем роль текущего пользователя
        val doctorDoc = firestore.collection("doctors").document(currentUser.uid).get().await()
        if (doctorDoc.exists()) {
            userRole = "doctor"
            // Загружаем пациентов прикрепленных к этому врачу
            firestore.collection("patients")
                .whereEqualTo("doctorId", currentUser.uid)
                .get()
                .addOnSuccessListener { result ->
                    patients = result.toObjects(Patient::class.java)
                    isLoading = false
                }
            return@LaunchedEffect
        }
        val patientDoc = firestore.collection("patients").document(currentUser.uid).get().await()
        if (patientDoc.exists()) {
            userRole = "patient"
            // Загружаем данные врача, прикрепленного к пациенту
            val patient = patientDoc.toObject(Patient::class.java)
            val docId = patient?.doctorId
            if (!docId.isNullOrEmpty()) {
                firestore.collection("doctors").document(docId).get()
                    .addOnSuccessListener {
                        doctors = listOf(it.toObject(Doctor::class.java)!!)
                        isLoading = false
                    }
            } else {
                doctors = emptyList()
                isLoading = false
            }
            return@LaunchedEffect
        }
        userRole = "unknown"
        isLoading = false
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(Modifier.padding(16.dp)) {
        Text("Вы вошли как: ${userRole?.capitalize() ?: "неизвестно"}", style = MaterialTheme.typography.h6)

        Spacer(Modifier.height(16.dp))

        if (userRole == "doctor") {
            Text("Ваши пациенты:", style = MaterialTheme.typography.h5)
            if (patients.isEmpty()) {
                Text("Пациенты отсутствуют")
            } else {
                LazyColumn {
                    items(patients) { patient ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { navController.navigate("about/${patient.uid}") }  // Обработка клика
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                Text(text = "Name: ${patient.name}")
                                Text(text = "Email: ${patient.email}")
                            }
                        }
                    }
                }
            }
        } else if (userRole == "patient") {
            Text("Ваш врач:", style = MaterialTheme.typography.h5)
            if (doctors.isEmpty()) {
                Text("Доктор не назначен")
            } else {
                doctors.forEach { doc ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(Modifier.padding(8.dp)) {
                            Text(text = "Name: ${doc.name}")
                            Text(text = "Email: ${doc.email}")
                            Text(text = "Специальность: ${doc.specialty}")
                        }
                    }
                }
            }
        } else {
            Text("Неизвестная роль")
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = { auth.signOut(); navController.navigate("login") }) {
            Text("Выйти")
        }
    }

}
