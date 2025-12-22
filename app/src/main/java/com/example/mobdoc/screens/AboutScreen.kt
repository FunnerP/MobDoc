package com.example.mobdoc.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobdoc.Models.MedHis
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch


@Composable
fun AboutScreen(patientId: String, navController: NavController) {
    val firestore = Firebase.firestore

    var medHistories by remember { mutableStateOf<List<MedHis>>(emptyList()) }
    var newHistoryText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(patientId) {
        // Подписываемся на историю болезни пациента
        firestore.collection("medHis")
            .whereEqualTo("patientId", patientId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    medHistories = snapshot.toObjects(MedHis::class.java)
                    isLoading = false
                }
            }
    }

    Column(Modifier.padding(16.dp)) {
        Text("История болезни пациента", style = MaterialTheme.typography.h5)


        if (isLoading) {
            CircularProgressIndicator()
            return@Column
        }

        LazyColumn(Modifier.weight(1f)) {
            items(medHistories) { medHis ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        Text(text = "Дата: ${medHis.date}", style = MaterialTheme.typography.subtitle2)
                        Text(text = medHis.history)
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = newHistoryText,
            onValueChange = { newHistoryText = it },
            label = { Text("Добавить новую запись") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (newHistoryText.isNotBlank()) {
                    val newMedHis = MedHis(
                        date = Timestamp.now(),
                        history = newHistoryText,
                        patientId = patientId
                    )
                    scope.launch {
                        firestore.collection("medHis").add(newMedHis)
                        newHistoryText = ""
                    }
                }
            },modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Добавить запись")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Назад")
        }
    }
}
