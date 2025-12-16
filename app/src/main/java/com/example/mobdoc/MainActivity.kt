package com.example.mobdoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobdoc.ViewModels.UserViewModel
import com.example.mobdoc.screens.UserEditScreen
import com.example.mobdoc.screens.UserListScreen
import com.example.mobdoc.ui.theme.MobDocTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobDocTheme() {
                val navController= rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "userList"
                )
                {
                    composable ("userList") {
                        UserListScreen(
                            onAddUser = {
                                navController.navigate("userEdit")
                            },
                            onEditUser = { user ->
                                navController.navigate("userEdit/${user.id}")
                            }
                        )
                    }
                    composable("userEdit") {
                        UserEditScreen (onBack = { navController.popBackStack() })
                    }
                    composable("userEdit/{userId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId") ?: ""
                        val viewModel: UserViewModel= viewModel()
                        val user by viewModel.user.collectAsState()
                        LaunchedEffect (userId) {
                            viewModel.getUser(userId)
                        }
                        if(user!=null)
                            UserEditScreen(user,onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MobDocTheme() {
        Greeting("Android")
    }
}