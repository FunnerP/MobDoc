package com.example.mobdoc.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.mobdoc.LoginActivity
import com.example.mobdoc.ui.theme.MobDocTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@Composable
fun RegisterForm() {

    Surface {
        var credentials by remember { mutableStateOf(CredentialsRegister()) }
        val context = LocalContext.current

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            LoginField(
                value = credentials.login,
                onChange = { data -> credentials = credentials.copy(login = data) },
                modifier = Modifier.fillMaxWidth()
            )
            PasswordField(
                value = credentials.pwd,
                onChange = { data -> credentials = credentials.copy(pwd = data) },
                submit = {
                    if (!checkCredentialsRegister(credentials, context)) credentials =
                        CredentialsRegister()
                },
                label = "Password",
                modifier = Modifier.fillMaxWidth()
            )
            PasswordField(
                value = credentials.repeatPwd,
                onChange = { data -> credentials = credentials.copy(repeatPwd = data) },
                submit = {
                    if (!checkCredentialsRegister(credentials, context)) credentials =
                        CredentialsRegister()
                },
                label = "Repeat Password",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.height(20.dp))
            Button (
                onClick = {
                    if (!checkCredentialsRegister(credentials, context))
                    {
                        credentials = CredentialsRegister()
                    }
                },
                enabled = credentials.isNotEmpty(),
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }

        }
    }
}



data class CredentialsRegister(
    var login: String = "",
    var pwd: String = "",
    var repeatPwd: String=""
) {
    fun isNotEmpty(): Boolean {
        return login.isNotEmpty() && pwd.isNotEmpty()
                &&repeatPwd.isNotEmpty()
    }
}

fun checkCredentialsRegister(creds: CredentialsRegister, context: Context): Boolean {
    if (creds.isNotEmpty() && creds.pwd==creds.repeatPwd) {
        val auth: FirebaseAuth= Firebase.auth
        auth.createUserWithEmailAndPassword(creds.login,creds.pwd).
        addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(context,"Register successfully", Toast.LENGTH_LONG).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
                (context as Activity).finish()
            }
            else
            {
                Toast.makeText(context,"Register not successfully", Toast.LENGTH_LONG).show()
            }
        }
        return true
    } else {
        Toast.makeText(context, "Wrong Credentials", Toast.LENGTH_SHORT).show()
        return false
    }
}

@Composable
fun HyperlinkText(
    modifier: Modifier,
    fullText: String,
    linkText: List<String>,
    linkTextColor: Color = Color.Blue,
    linkTextFontWeight: FontWeight = FontWeight.Medium,
    linkTextDecoration: TextDecoration = TextDecoration.Underline,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    val annotatedString = buildAnnotatedString {
        append(fullText)
        linkText.forEachIndexed { index, link ->
            val startIndex = fullText.indexOf(link)
            val endIndex = startIndex + link.length
            addStyle(
                style = SpanStyle(
                    color = linkTextColor,
                    fontSize = fontSize,
                    fontWeight = linkTextFontWeight,
                    textDecoration = linkTextDecoration
                ),
                start = startIndex,
                end = endIndex
            )
            addStringAnnotation(
                tag = "URL",
                annotation = "Register",
                start = startIndex,
                end = endIndex
            )
        }
        addStyle(
            style = SpanStyle(
                fontSize = fontSize
            ),
            start = 0,
            end = fullText.length
        )
    }
}

@Preview(showBackground = true, device = "id:Nexus One", showSystemUi = true)
@Composable
fun RegisterFormPreview() {
    MobDocTheme {
        RegisterForm()
    }
}

@Preview(showBackground = true, device = "id:Nexus One", showSystemUi = true)
@Composable
fun RegisterFormPreviewDark() {
    MobDocTheme (darkTheme = true) {
        RegisterForm()
    }
}