package com.example.mobdoc.screens

import ads_mobile_sdk.h4
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.mobdoc.Models.DataState
import com.example.mobdoc.Models.User
import com.example.mobdoc.ViewModels.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.*

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.navigation.compose.*


@Composable
fun Home(viewModel: HomeViewModel){
    val auth = FirebaseAuth.getInstance()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material.Text(
            text = "Добро пожаловать!",
        )
        Spacer(Modifier.height(24.dp))
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        androidx.compose.material.Text(
            text = "Это приложения для врача, специализирующегося на лечении " +
                    "хронических заболеваний, основнйо задачей является, " +
                    "улучшить качество медицинской " +
                    "помощи пациентам. Приложения может способствовать " +
                    "лучшему пониманию историй болезни пациентов, оптимизации " +
                    "лечения и повышению эффективности взаимодействия между врачом и пациентом",
        )
    }
    Button(onClick = {
        auth.signOut()

    }) {
        Text(text = "Выйти")
    }
}
@Composable
fun ShowLazyList(users: MutableList<User>){
    LazyColumn {
        items(users){
                user->CardItem(user)
        }
    }
}

@Composable
fun CardItem(user: User){
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(user.name)
            .build(),
        imageLoader = ImageLoader.Builder(LocalContext.current)
            .components {
            }
            .build()
    )
    Card(modifier = Modifier.fillMaxSize().height(200.dp).padding(10.dp),
        colors = CardDefaults.cardColors(contentColor = Color.Black,
            containerColor = Color.White)) {

        Image(
            painter = painter,
            modifier = Modifier.fillMaxWidth().height(150.dp),
            contentDescription = "My content description",
            contentScale = ContentScale.Fit
        )

        Text(
            text = user.name,
            fontSize = MaterialTheme.typography.labelLarge.fontSize,
            modifier = Modifier
                .fillMaxWidth().
                align(alignment = Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            color = Color.Blue
        )
    }
}