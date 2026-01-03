package com.example.frontened.presentation.patientScreen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.AppointmentDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAppointmentScreen(
    navController: NavController,
    viewModel: PatientViewModel = hiltViewModel()
) {
    val state by viewModel.appointmentsState.collectAsState()

    // Healthcare colors (matching login/signup blue theme)
    val primaryBlue = Color(0xFF1976D2)
    val accentPurple = Color(0xFF7B1FA2)
    val backgroundColor = Color(0xFFF5F5F5)
    val lightBlue = Color(0xFFE3F2FD)

    LaunchedEffect(Unit) {
        viewModel.loadMyAppointments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Appointments",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryBlue,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { viewModel.loadMyAppointments() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = 75.dp)
                .background(backgroundColor)
        ) {
            when (state) {
                is ResultState.Loading -> {
                    LoadingState(primaryBlue)
                }

                is ResultState.Error -> {
                    ErrorState(
                        error = (state as ResultState.Error<List<AppointmentDto>>).message,
                        onRetry = { viewModel.loadMyAppointments() },
                        primaryColor = primaryBlue
                    )
                }

                is ResultState.Success -> {
                    val appointments = (state as ResultState.Success<List<AppointmentDto>>).data

                    if (appointments.isEmpty()) {
                        EmptyAppointmentsState(primaryBlue)
                    } else {
                        AppointmentsList(
                            appointments = appointments,
                            primaryColor = primaryBlue,
                            accentColor = accentPurple,
                            viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentsList(
    appointments: List<AppointmentDto>,
    primaryColor: Color,
    accentColor: Color,
    viewModel: PatientViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header card with summary
        item {
            SummaryCard(
                totalAppointments = appointments.size,
                upcomingCount = appointments.count {
                    it.status.equals("CONFIRMED", ignoreCase = true) ||
                            it.status.equals("PENDING", ignoreCase = true)
                },
                completedCount = appointments.count {
                    it.status.equals("COMPLETED", ignoreCase = true)
                },
                primaryColor = primaryColor,
                accentColor = accentColor
            )
        }

        // Appointments list
        items(appointments) { appointment ->
            AppointmentItem(
                appointment = appointment,
                primaryColor = primaryColor,
                onCancelClick = { appointmentId ->
                    viewModel.cancelAppointment(appointmentId)
                }
            )
        }
    }
}

@Composable
fun SummaryCard(
    totalAppointments: Int,
    upcomingCount: Int,
    completedCount: Int,
    primaryColor: Color,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(primaryColor, accentColor)
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    label = "Total",
                    value = totalAppointments.toString(),
                    icon = Icons.Default.CalendarMonth
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(60.dp)
                        .padding(horizontal = 8.dp),
                    color = Color.White.copy(alpha = 0.3f),
                    thickness = 1.dp
                )




                SummaryItem(
                    label = "Completed",
                    value = completedCount.toString(),
                    icon = Icons.Default.CheckCircle
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentItem(
    appointment: AppointmentDto,
    primaryColor: Color,
    onCancelClick: (String) -> Unit
) {
    val statusColor = when (appointment.status.uppercase()) {
        "PENDING" -> Color(0xFFFFA726)
        "CONFIRMED" -> Color(0xFF66BB6A)
        "COMPLETED" -> primaryColor
        "CANCELLED" -> Color(0xFFEF5350)
        else -> Color.Gray
    }

    val statusIcon = when (appointment.status.uppercase()) {
        "PENDING" -> Icons.Default.Schedule
        "CONFIRMED" -> Icons.Default.CheckCircle
        "COMPLETED" -> Icons.Default.Done
        "CANCELLED" -> Icons.Default.Cancel
        else -> Icons.Default.Info
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Doctor Icon
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = primaryColor.copy(alpha = 0.15f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalHospital,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Doctor Appointment",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF333333)
                        )
                        Text(
                            text = "ID: ${appointment._id.takeLast(8)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = appointment.status.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color(0xFFEEEEEE)
            )

            // Appointment Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppointmentDetailItem(
                    icon = Icons.Default.DateRange,
                    label = "Date",
                    value = formatDate(appointment.date),
                    color = primaryColor
                )

                AppointmentDetailItem(
                    icon = Icons.Default.Schedule,
                    label = "Time",
                    value = appointment.startTime,
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppointmentDetailItem(
                    icon = Icons.Default.AccessTime,
                    label = "Duration",
                    value = "${appointment.startTime} - ${appointment.endTime}",
                    color = primaryColor
                )
            }

            // Action button for confirmed appointments
            // Action button for cancellable appointments
            if (appointment.status.equals("BOOKED", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { onCancelClick(appointment._id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Cancel Appointment",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cancel Appointment",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        }
    }
}

@Composable
fun AppointmentDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
        }
    }
}

@Composable
fun LoadingState(primaryColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = primaryColor
            )
            Text(
                "Loading your appointments...",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    primaryColor: Color
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFEBEE)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = Color(0xFFEF5350),
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    "Failed to Load Appointments",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFC62828),
                    textAlign = TextAlign.Center
                )
                Text(
                    error,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun EmptyAppointmentsState(primaryColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color(0xFFE3F2FD)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.EventBusy,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Text(
                "No Appointments Yet",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF333333)
            )
            Text(
                "Your booked appointments will appear here",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { /* Navigate to book appointment */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Book an Appointment", fontSize = 15.sp)
            }
        }
    }
}

// Helper function
@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(dateString: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val date = LocalDateTime.parse(dateString, formatter)
        "${date.dayOfMonth} ${date.month.name.take(3)} ${date.year}"
    } catch (e: Exception) {
        dateString.take(10)
    }
}