package com.example.frontened.presentation.SignupScreen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontened.data.dto.RegisterRequestDto
import com.example.frontened.presentation.components.CustomTextField
import com.example.frontened.presentation.navigation.AppRoutes
import com.example.frontened.utils.JwtUtils
import com.example.frontened.utils.TokenManager


@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    navController: NavController,
    tokenManager: TokenManager
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var role by rememberSaveable { mutableStateOf("PATIENT") }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var fee by rememberSaveable { mutableStateOf("") }
    var speciality by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var dob by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE3F2FD),
                        Color(0xFFFFFFFF)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = Color(0xFF1976D2)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Join HealthCare",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )

                Text(
                    text = "Create your account to get started",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF616161),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Role Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "I am a",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF424242)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RoleOption(
                            text = "Patient",
                            icon = Icons.Default.Person,
                            selected = role == "PATIENT",
                            modifier = Modifier.weight(1f)
                        ) { role = "PATIENT" }

                        RoleOption(
                            text = "Doctor",
                            icon = Icons.Default.MedicalServices,
                            selected = role == "DOCTOR",
                            modifier = Modifier.weight(1f)
                        ) { role = "DOCTOR" }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Basic Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF424242)
                    )

                    CustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = Icons.Default.Person,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    CustomTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = Icons.Default.Phone,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    CustomTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = Icons.Default.Lock,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Gender Selection
                    Column {
                        Text(
                            text = "Gender",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF424242)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            GenderOption(
                                text = "Male",
                                selected = gender == "Male",
                                modifier = Modifier.weight(1f)
                            ) { gender = "Male" }

                            GenderOption(
                                text = "Female",
                                selected = gender == "Female",
                                modifier = Modifier.weight(1f)
                            ) { gender = "Female" }

                            GenderOption(
                                text = "Other",
                                selected = gender == "Other",
                                modifier = Modifier.weight(1f)
                            ) { gender = "Other" }
                        }
                    }

                    CustomTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = "Date of Birth (YYYY-MM-DD)",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = Icons.Default.DateRange,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Doctor Details Card (Conditional)
            if (role == "DOCTOR") {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF3E5F5)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                tint = Color(0xFF7B1FA2),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Professional Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF7B1FA2)
                            )
                        }

                        CustomTextField(
                            value = speciality,
                            onValueChange = { speciality = it },
                            label = "Medical Speciality",
                            singleLine = true,
                            leadingIcon = Icons.Default.LocalHospital,
                            color = Color(0xFF424242)
                        )

                        CustomTextField(
                            value = fee,
                            onValueChange = { fee = it },
                            label = "Consultation Fee (₹)",
                            singleLine = true,
                            leadingIcon = Icons.Default.Money,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            color = Color(0xFF424242)
                        )

                        CustomTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = "Enter city",
                            singleLine = false,
                            leadingIcon = Icons.Default.LocationOn,
                            color = Color(0xFF424242)
                        )

                        CustomTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = "Enter Address",
                            singleLine = false,
                            leadingIcon = Icons.Default.LocationOn,
                            color = Color(0xFF424242)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {
                    if (
                        name.isEmpty() ||
                        email.isEmpty() ||
                        phone.isEmpty() ||
                        password.isEmpty() ||
                        gender.isEmpty() ||
                        dob.isEmpty() ||
                        (role == "DOCTOR" &&
                                (fee.isEmpty() || speciality.isEmpty() || city.isEmpty() || address.isEmpty()))
                    ) {
                        Toast.makeText(context, "Fill all required fields", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }

                    viewModel.registerUser(
                        RegisterRequestDto(
                            role = role,
                            name = name,
                            email = email,
                            mobileNumber = phone,
                            password = password,
                            gender = gender,
                            dob = dob,
                            fee = if (role == "DOCTOR") fee.toIntOrNull() else null,
                            speciality = if (role == "DOCTOR") speciality else null,
                            city = if (role == "DOCTOR") city.trim() else null,
                            address = if (role == "DOCTOR") address.trim() else null
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2)
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    "Create Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    color = Color(0xFF616161),
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = { navController.navigate(AppRoutes.Login.route) }
                ) {
                    Text(
                        "Login",
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Loading Overlay
        if (state.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1976D2))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Creating your account...", color = Color(0xFF424242))
                    }
                }
            }
        }
    }

    LaunchedEffect(state.message) {
        if (state.message != null) {

            Toast.makeText(
                context,
                state.message,
                Toast.LENGTH_SHORT
            ).show()

            val token = tokenManager.getAccessToken()
            val role = token?.let { JwtUtils.getRole(it) }

            when (role) {
                "PATIENT" -> {
                    navController.navigate(AppRoutes.PatientScreen.route) {
                        popUpTo(AppRoutes.SignUp.route) { inclusive = true }
                    }
                }

                "DOCTOR" -> {
                    navController.navigate(AppRoutes.DoctorDashBoard.route) {
                        popUpTo(AppRoutes.SignUp.route) { inclusive = true }
                    }
                }

                else -> {
                    Toast.makeText(
                        context,
                        "Invalid role",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        viewModel.clearState()
    }


    state.error?.let { error ->
        LaunchedEffect(error) {
            val userFriendlyMessage = when {
                error.contains("User already registered") ||
                        error.contains("already exists") ->
                    "This email or phone number is already registered. Please use different details or try logging in."
                error.contains("400") ->
                    "Invalid input. Please check your details."
                error.contains("500") ->
                    "Server error. Please try again later."
                error.contains("Network") || error.contains("Unable to resolve host") ->
                    "Network error. Please check your internet connection."
                else -> error
            }
            Toast.makeText(context, userFriendlyMessage, Toast.LENGTH_LONG).show()
        }

    }
}


@Composable
private fun RoleOption(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                Color(0xFF1976D2)
            else
                Color(0xFFF5F5F5)
        ),
        modifier = modifier
            .clickable { onClick() }
            .height(80.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 4.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else Color(0xFF616161),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                color = if (selected) Color.White else Color(0xFF424242),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun GenderOption(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (selected) Color(0xFF1976D2) else Color(0xFFF5F5F5),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.White else Color(0xFF424242),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}