package com.example.mobdoc.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mobdoc.Models.Doctor
import com.example.mobdoc.Models.Patient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

@Composable
fun LoginScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val firestore = Firebase.firestore

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Patient") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Вход / Регистрация", style = MaterialTheme.typography.h5, modifier = Modifier.padding(bottom = 16.dp))

        // Выбор роли
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = { role = "doctor" },
                colors = if (role == "doctor") ButtonDefaults.buttonColors(MaterialTheme.colors.primary) else ButtonDefaults.outlinedButtonColors()
            ) { Text("Доктор") }

            Spacer(Modifier.width(16.dp))

            Button(
                onClick = { role = "patient" },
                colors = if (role == "patient") ButtonDefaults.buttonColors(MaterialTheme.colors.primary) else ButtonDefaults.outlinedButtonColors()
            ) { Text("Пациент") }
        }

        Spacer(Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ваше ФИО") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = {
                    errorMessage = null
                    loading = true
                    auth.signInWithEmailAndPassword(email.trim(), password)
                        .addOnCompleteListener { task ->
                            loading = false
                            if (task.isSuccessful) {
                                navController.navigate("main") { popUpTo("login") { inclusive = true } }
                            } else {
                                errorMessage = task.exception?.localizedMessage ?: "Ошибка входа"
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Войти")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    errorMessage = null
                    loading = true
                    auth.createUserWithEmailAndPassword(email.trim(), password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = task.result?.user?.uid ?: ""
                                // Запись в Firestore по роли
                                val docRef = when(role) {
                                    "doctor" -> {
                                        val doctor = Doctor(uid = uid, name = name, email = email, specialty = "")
                                        firestore.collection("doctors").document(uid).set(doctor)
                                    }
                                    else -> {
                                        val patient = Patient(uid = uid, name = name, email = email, doctorId = "")
                                        firestore.collection("patients").document(uid).set(patient)
                                    }
                                }
                                docRef.addOnCompleteListener { saveTask ->
                                    loading = false
                                    if (saveTask.isSuccessful) {
                                        navController.navigate("main") { popUpTo("login") { inclusive = true } }
                                    } else {
                                        errorMessage = "Ошибка при сохранении данных пользователя"
                                    }
                                }
                            } else {
                                loading = false
                                errorMessage = task.exception?.localizedMessage ?: "Ошибка регистрации"
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зарегистрироваться")
            }
        }

        errorMessage?.let {
            Spacer(Modifier.height(16.dp))
            Text(text = it, color = Color.Red)
        }
    }
}