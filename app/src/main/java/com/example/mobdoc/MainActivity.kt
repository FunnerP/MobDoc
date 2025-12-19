package com.example.mobdoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.mobdoc.ViewModels.HomeViewModel
import com.example.mobdoc.screens.Home
import com.example.mobdoc.ui.theme.MobDocTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobDocTheme {
                Surface(color = MaterialTheme.colors.background) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    NavHost(
        navController = navController,
        startDestination = if (currentUser == null) "login" else "main"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("main") { MainScreen() }
    }
}

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
        Text("Вход / Регистрация", style = MaterialTheme.typography.h5, modifier = Modifier.padding(bottom = 24.dp))

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
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    errorMessage = null
                    loading = true
                    auth.createUserWithEmailAndPassword(email.trim(), password)
                        .addOnCompleteListener { task ->
                            loading = false
                            if (task.isSuccessful) {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
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

@Composable
fun MainScreen() {
    val navController= rememberNavController()
    Column(Modifier.padding(top = 24.dp, bottom = 8.dp,)){
        NavHost(navController, startDestination = NavRoutes.Home.route, modifier = Modifier.weight(1f)){
            composable(NavRoutes.Home.route){ Home(viewModel()) }
            composable(NavRoutes.Contacts.route){Contacts()}
            composable(NavRoutes.About.route){About()}
        }
        BottomNavigationBar(navController=navController)
    }
}
@Composable
fun BottomNavigationBar(navController: NavController){
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute=backStackEntry?.destination?.route
        NavBarItems.BarItems.forEach { navItem->
            NavigationBarItem(
                selected = currentRoute==navItem.route,
                onClick = {
                    navController.navigate(navItem.route){
                        popUpTo(navController.graph.findStartDestination().id){saveState=true}
                        launchSingleTop=true
                        restoreState=true
                    }
                } ,
                icon = { Icon(imageVector = navItem.image,
                    contentDescription = navItem.title) },
                label = {
                    Text(text = navItem.title)
                }
            )
        }
    }
}
object NavBarItems{
    val BarItems=listOf(
        BarItem(title = "Home", image = Icons.Filled.Home, route = "home"),
        BarItem(title = "Contacts", image = Icons.Filled.Face, route = "contacts"),
        BarItem(title = "About", image = Icons.Filled.Info, route = "about"),
    )
}
data class BarItem(
    val title: String,
    val image: ImageVector,
    val route: String
)
@Composable
fun Contacts(){
    Text("Contact page", fontSize = 30.sp)
}
@Composable
fun About(){
    Text("About page", fontSize = 30.sp)
}
sealed class NavRoutes(val route: String){
    object Home: NavRoutes("home")
    object Contacts: NavRoutes("contacts")
    object About: NavRoutes("about")
}