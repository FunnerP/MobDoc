package com.example.mobdoc.screens

import ads_mobile_sdk.id
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobdoc.Models.MedHis
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch


@Composable
fun AboutScreen(patientId: String, navController: NavController) {
    val firestore = Firebase.firestore

    var medHistories by remember { mutableStateOf<List<MedHis>>(emptyList()) }
    var newHistoryText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Подписка на обновления из Firestore с получением id документа
    LaunchedEffect(patientId) {
        firestore.collection("medHis")
            .whereEqualTo("patientId", patientId)
//            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    isLoading = false
                    errorMessage = "Ошибка загрузки: ${error.message}"
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    medHistories = snapshot.documents.map { doc ->
                        doc.toObject(MedHis::class.java)!!.copy(id = doc.id)
                    }
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

        if (errorMessage != null) {
            Text(errorMessage!!, color = Color.Red, modifier = Modifier.padding(vertical = 8.dp))
        }

        if (medHistories.isEmpty()){
            Text("Записи отсутствуют")
        } else {
            LazyColumn(Modifier.weight(1f)) {
                items(medHistories) { medHis ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = "Дата: ${medHis.date.toDate()}",
                                    style = MaterialTheme.typography.subtitle2
                                )
                                Text(text = medHis.history)
                            }
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        try {
                                            firestore.collection("medHis")
                                                .document(medHis.id)
                                                .delete()
                                        } catch (e: Exception) {
                                            errorMessage = "Ошибка удаления: ${e.message}"
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить запись")
                            }
                        }
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
                        patientId = patientId,
                    )
                    scope.launch {
                        try {
                            firestore.collection("medHis").add(newMedHis)
                            newHistoryText = ""
                        } catch (e: Exception) {
                            errorMessage = "Ошибка добавления: ${e.message}"
                        }
                    }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Добавить запись")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Назад")
        }
    }
}