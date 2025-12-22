package com.example.mobdoc.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Войти в аккаунт", style = MaterialTheme.typography.h5, modifier = Modifier.padding(bottom = 16.dp))

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
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                errorMessage = task.exception?.localizedMessage ?: "Ошибка входа"
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Войти")
            }
        }

        errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colors.error, modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Регистрация")
        }
    }
}