package com.example.mobdoc

import androidx.activity.compose.setContent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.mobdoc.screens.LoginForm
import com.example.mobdoc.ui.theme.MobDocTheme


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobDocTheme {
                LoginForm()
            }
        }
    }
}