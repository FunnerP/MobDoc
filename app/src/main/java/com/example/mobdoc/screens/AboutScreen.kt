package com.example.mobdoc.screens

import ads_mobile_sdk.id
import androidx.compose.foundation.clickable
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
import java.util.Locale


@Composable
fun AboutScreen(
    patientId: String,
    navController: NavController
) {
    val firestore = Firebase.firestore

    var records by remember { mutableStateOf<List<MedHis>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var editRecord by remember { mutableStateOf<MedHis?>(null) }
    var recordHistory by remember { mutableStateOf("") }

    LaunchedEffect(patientId) {
        firestore.collection("medhis")
            .whereEqualTo("patientId", patientId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    isLoading = false
                    return@addSnapshotListener
                }
                records = snapshot?.documents?.map {
                    val rec = it.toObject(MedHis::class.java)!!
                    rec.copy(id = it.id)
                } ?: emptyList()
                isLoading = false
            }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("История болезни пациента", style = MaterialTheme.typography.h6)

        Spacer(Modifier.height(12.dp))

        LazyColumn(Modifier.weight(1f)) {
            items(records, key = { it.id }) { record ->
                Card(Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        editRecord = record
                        recordHistory = record.history
                        showDialog = true
                    },
                    backgroundColor = Color(0xFFF0F0F0)) {
                    Column(Modifier.padding(8.dp)) {
                        Text(text = "Дата: ${java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(record.date.toDate())}")
                        Text(text = "История: ${record.history}")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            editRecord = null
            recordHistory = ""
            showDialog = true
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Добавить запись")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(if (editRecord == null) "Добавить запись" else "Изменить запись")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = recordHistory,
                        onValueChange = { recordHistory = it },
                        label = { Text("История болезни") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (recordHistory.isNotBlank()) {
                        val recordsRef = firestore.collection("medhis")

                        if (editRecord == null) {
                            // Добавляем новую запись с текущим Timestamp и patientId
                            val newRecord = MedHis(
                                date = Timestamp.now(),
                                history = recordHistory,
                                patientId = patientId
                            )
                            recordsRef.add(newRecord)
                        } else {
                            // Обновляем существующую запись (обновляем только поле history)
                            recordsRef.document(editRecord!!.id).update("history", recordHistory)
                        }
                    }
                    showDialog = false
                }) {
                    Text("Сохранить")
                }
            },
            dismissButton = {
                Row {
                    if (editRecord != null) {
                        TextButton(onClick = {
                            firestore.collection("medhis")
                                .document(editRecord!!.id)
                                .delete()
                            showDialog = false
                        }) {
                            Text("Удалить", color = MaterialTheme.colors.error)
                        }
                    }
                    TextButton(onClick = { showDialog = false }) {
                        Text("Отмена")
                    }
                }
            }
        )
    }
}