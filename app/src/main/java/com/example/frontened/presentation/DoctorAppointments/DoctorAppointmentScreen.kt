package com.example.frontened.presentation.DoctorAppointments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.DoctorAppointmentDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorAppointmentsScreen(
    navController: NavController,
    viewModel: DoctorAppointmentsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Healthcare green color scheme
    val primaryGreen = Color(0xFF00897B)
    val backgroundColor = Color(0xFFF5F5F5)

    LaunchedEffect(Unit) {
        viewModel.loadAppointments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Appointments",
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
                    containerColor = primaryGreen,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { viewModel.loadAppointments() }) {
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
                    LoadingState(primaryGreen)
                }

                is ResultState.Error -> {
                    ErrorState(
                        error = (state as ResultState.Error).message,
                        onRetry = { viewModel.loadAppointments() },
                        primaryColor = primaryGreen
                    )
                }

                is ResultState.Success -> {
                    val appointments = (state as ResultState.Success<List<DoctorAppointmentDto>>).data

                    if (appointments.isEmpty()) {
                        EmptyAppointments(primaryGreen)
                    } else {
                        AppointmentContent(
                            appointments = appointments,
                            primaryColor = primaryGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentContent(
    appointments: List<DoctorAppointmentDto>,
    primaryColor: Color
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Statistics Card
        item {
            StatisticsCard(
                appointments = appointments,
                primaryColor = primaryColor
            )
        }

        // Appointments List
        items(appointments) { appointment ->
            AppointmentCard(
                appointment = appointment,
                primaryColor = primaryColor
            )
        }
    }
}

@Composable
fun StatisticsCard(
    appointments: List<DoctorAppointmentDto>,
    primaryColor: Color
) {
    val bookedCount = appointments.count { it.status.equals("BOOKED", ignoreCase = true) }
    val cancelledCount = appointments.count { it.status.equals("CANCELLED", ignoreCase = true) }
    val completedCount = appointments.count { it.status.equals("COMPLETED", ignoreCase = true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "Total",
                value = appointments.size.toString(),
                icon = Icons.Default.CalendarMonth
            )

            VerticalDivider(
                modifier = Modifier
                    .height(60.dp)
                    .padding(horizontal = 8.dp),
                color = Color.White.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            StatItem(
                label = "Booked",
                value = bookedCount.toString(),
                icon = Icons.Default.EventAvailable
            )

            VerticalDivider(
                modifier = Modifier
                    .height(60.dp)
                    .padding(horizontal = 8.dp),
                color = Color.White.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            StatItem(
                label = "Completed",
                value = completedCount.toString(),
                icon = Icons.Default.CheckCircle
            )
        }
    }
}

@Composable
fun StatItem(
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

@Composable
fun AppointmentCard(
    appointment: DoctorAppointmentDto,
    primaryColor: Color
) {
    val statusColor = when (appointment.status.uppercase()) {
        "BOOKED" -> Color(0xFF66BB6A)
        "PENDING" -> Color(0xFFFFA726)
        "CONFIRMED" -> Color(0xFF42A5F5)
        "COMPLETED" -> primaryColor
        "CANCELLED" -> Color(0xFFEF5350)
        else -> Color.Gray
    }

    val statusIcon = when (appointment.status.uppercase()) {
        "BOOKED" -> Icons.Default.EventAvailable
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
            // Header with patient info and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Patient Avatar
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = statusColor.copy(alpha = 0.2f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Patient",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = appointment.patientId.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF333333)
                        )

                        Text(
                            text = appointment.patientId.mobileNumber,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF333333)
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
                DetailItem(
                    icon = Icons.Default.DateRange,
                    label = "Date",
                    value = formatDate(appointment.date),
                    color = primaryColor
                )

                DetailItem(
                    icon = Icons.Default.Schedule,
                    label = "Time",
                    value = "${appointment.startTime}",
                    color = primaryColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                DetailItem(
                    icon = Icons.Default.AccessTime,
                    label = "Duration",
                    value = "${appointment.startTime} - ${appointment.endTime}",
                    color = primaryColor
                )
            }

            // Action buttons for BOOKED status
            if (appointment.status.equals("BOOKED", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
//                    OutlinedButton(
//                        onClick = { /* Handle cancel */ },
//                        modifier = Modifier.weight(1f),
//                        shape = RoundedCornerShape(8.dp),
//                        colors = ButtonDefaults.outlinedButtonColors(
//                            contentColor = Color(0xFFEF5350)
//                        )
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Close,
//                            contentDescription = null,
//                            modifier = Modifier.size(16.dp)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("Cancel", fontSize = 13.sp)
//                    }

//                    Button(
//                        onClick = { /* Handle complete */ },
//                        modifier = Modifier.weight(1f),
//                        shape = RoundedCornerShape(8.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = primaryColor
//                        )
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Check,
//                            contentDescription = null,
//                            modifier = Modifier.size(16.dp)
//                        )
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("Complete", fontSize = 13.sp)
//                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(
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
                "Loading appointments...",
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
                    "Error Loading Appointments",
                    fontWeight = FontWeight.Bold,
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
                    )
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
fun EmptyAppointments(primaryColor: Color) {
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
                color = Color(0xFFE0F2F1)
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
                "Your appointments will appear here",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper function
fun formatDate(dateString: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val date = LocalDateTime.parse(dateString, formatter)
        "${date.dayOfMonth} ${date.month.name.take(3)} ${date.year}"
    } catch (e: Exception) {
        dateString.take(10)
    }
}