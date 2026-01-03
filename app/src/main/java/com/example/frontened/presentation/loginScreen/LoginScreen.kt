package com.example.frontened.presentation.loginScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontened.data.dto.LoginRequestData
import com.example.frontened.presentation.components.CustomTextField
import com.example.frontened.presentation.navigation.AppRoutes
import com.example.frontened.utils.JwtUtils
import com.example.frontened.utils.TokenManager


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel(),
    tokenManager: TokenManager
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LoginContent(
            navController = navController,
            onLoginClick = { email, password ->
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    viewModel.login(
                        LoginRequestData(
                            email = email,
                            password = password
                        )
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Please fill all the details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        if (state.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Signing you in...",
                            color = Color(0xFF424242)
                        )
                    }
                }
            }
        }
    }

//    state.message?.let {
//        navController.navigate(AppRoutes.PatientScreen.route){
//            popUpTo(AppRoutes.Login.route){ inclusive = true}
//        }
//    }

    LaunchedEffect(state.message) {
        if (state.message != null) {

            val token = tokenManager.getAccessToken()
            val role = token?.let { JwtUtils.getRole(it) }

            when (role) {
                "PATIENT" -> {
                    navController.navigate(AppRoutes.PatientScreen.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                }

                "DOCTOR" -> {
                    navController.navigate(AppRoutes.DoctorDashBoard.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                }

                else -> {
                    Toast.makeText(context, "Invalid role", Toast.LENGTH_SHORT).show()
                }
            }

           viewModel.clearState()

        }
    }

    state.error?.let {
        Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show()
    }
}


@Composable
private fun LoginContent(
    navController: NavController,
    onLoginClick: (String, String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val isDark = isSystemInDarkTheme()

    // Theme-aware colors (matching SignUp screen)
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE3F2FD),
            Color(0xFFFFFFFF)
        )
    )

    val primaryColor = Color(0xFF1976D2)
    val accentColor = Color(0xFF7B1FA2)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Logo Section with Medical Icon
            Card(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = primaryColor
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = "Healthcare Icon",
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome Text
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to access your healthcare account",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF616161),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Login Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {

                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Email Field
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Password Field
                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = Icons.Default.Lock,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        color = MaterialTheme.colorScheme.onSurface
                    )



                    // Login Button
                    Button(
                        onClick = { onLoginClick(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Login,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF3E5F5)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Color(0xFF7B1FA2),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Don't have an account?",
                        color = Color(0xFF424242),
                        fontSize = 14.sp
                    )
                    TextButton(
                        onClick = {
                            navController.navigate(AppRoutes.SignUp.route) {
                                popUpTo(AppRoutes.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Text(
                            text = "Sign Up",
                            color = Color(0xFF7B1FA2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Secure Healthcare Portal",
                    color = Color(0xFF757575),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}