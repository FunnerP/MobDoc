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
import com.example.mobdoc.Models.Doctor
import com.example.mobdoc.Models.Patient
import com.example.mobdoc.screens.LoginScreen
import com.example.mobdoc.screens.MainScreen
import com.example.mobdoc.ui.theme.MobDocTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

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

//@Composable
//fun MainScreen() {
//    val navController= rememberNavController()
//    Column(Modifier.padding(top = 24.dp, bottom = 8.dp,)){
//        NavHost(navController, startDestination = NavRoutes.Home.route, modifier = Modifier.weight(1f)){
//            composable(NavRoutes.Home.route){ Home() }
//            composable(NavRoutes.About.route){About()}
//        }
//        BottomNavigationBar(navController=navController)
//    }
//}
//@Composable
//fun BottomNavigationBar(navController: NavController){
//    NavigationBar {
//        val backStackEntry by navController.currentBackStackEntryAsState()
//        val currentRoute=backStackEntry?.destination?.route
//        NavBarItems.BarItems.forEach { navItem->
//            NavigationBarItem(
//                selected = currentRoute==navItem.route,
//                onClick = {
//                    navController.navigate(navItem.route){
//                        popUpTo(navController.graph.findStartDestination().id){saveState=true}
//                        launchSingleTop=true
//                        restoreState=true
//                    }
//                } ,
//                icon = { Icon(imageVector = navItem.image,
//                    contentDescription = navItem.title) },
//                label = {
//                    Text(text = navItem.title)
//                }
//            )
//        }
//    }
//}
//object NavBarItems{
//    val BarItems=listOf(
//        BarItem(title = "Home", image = Icons.Filled.Home, route = "home"),
//        BarItem(title = "About", image = Icons.Filled.Info, route = "about"),
//    )
//}
//data class BarItem(
//    val title: String,
//    val image: ImageVector,
//    val route: String
//)
//@Composable
//fun About(){
//    Text("About page", fontSize = 30.sp)
//}
//sealed class NavRoutes(val route: String){
//    object Home: NavRoutes("home")
//    object About: NavRoutes("about")
//}