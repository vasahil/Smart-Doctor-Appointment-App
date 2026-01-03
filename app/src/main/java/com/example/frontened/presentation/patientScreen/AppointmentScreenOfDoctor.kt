package com.example.frontened.presentation.patientScreen

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.DoctorDto
import com.example.frontened.data.dto.SlotDto
import com.example.frontened.utils.AppointmentReminderScheduler
import com.example.frontened.utils.LocationProvider
import com.example.frontened.utils.NotificationHelper
import com.example.frontened.utils.RequestLocationPermission
import com.example.frontened.utils.buildGoogleMapsRouteUrl

import com.example.frontened.utils.openGoogleMaps

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoctorDetailScreen(
    navController: NavController,
    locationProvider: LocationProvider,
    viewModel: PatientViewModel = hiltViewModel()
) {
    val doctor = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<DoctorDto>("doctor")

    if (doctor == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Doctor not found")
        }
        return
    }

    Log.d("DOCTOR_UI", "doctor = $doctor")
    LaunchedEffect(Unit) {
        Log.d("DOCTOR_UI", "doctorId = ${doctor.id}")
    }


    val context = LocalContext.current
    val availabilityState by viewModel.state1.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()


    // Doctor location
    val doctorLat = doctor.location.coordinates[1]
    val doctorLng = doctor.location.coordinates[0]

    // Patient location state
    var patientLat by remember { mutableStateOf<Double?>(null) }
    var patientLng by remember { mutableStateOf<Double?>(null) }

    // Selected time slot and date
    var selectedTimeSlot by remember { mutableStateOf<SlotDto?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Show confirmation dialog
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Permission state
    var hasPermission by remember { mutableStateOf(false) }

    if (!hasPermission) {
        RequestLocationPermission {
            hasPermission = true
        }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            locationProvider.getLastLocation { lat, lng ->
                patientLat = lat
                patientLng = lng
            }
        }
    }

    // Load availability when date changes

    LaunchedEffect(selectedDate, doctor.id) {
        doctor.id?.let { id ->
            Log.d(
                "AVAILABILITY_UI",
                "Calling API with doctorId=${doctor.id}, date=$selectedDate"
            )
            viewModel.loadAvailability(
                doctorId = id,
                date = selectedDate.toString()
            )
        }
    }
//    LaunchedEffect(selectedDate) {
//        val doctorId = doctor.id
//        if (!doctor.id.isNullOrBlank()) {
//            viewModel.loadAvailability(
//                doctorId = doctorId,
//                date = selectedDate.toString()
//            )
//        }
//    }




    LaunchedEffect(bookingState) {
        when (bookingState) {

            is ResultState.Success -> {

//                val routeUrl =  openGoogleMaps(
//                    context = context,
//                    patientLat = patientLat!!,
//                    patientLng = patientLng!!,
//                    doctorLat = doctorLat,
//                    doctorLng = doctorLng
//                )


                val routeUrl = buildGoogleMapsRouteUrl(
                    patientLat = patientLat!!,
                    patientLng = patientLng!!,
                    doctorLat = doctorLat,
                    doctorLng = doctorLng
                )

                NotificationHelper.showAppointmentBooked(
                    context = context,
                    doctorName = doctor.name,
                    date = selectedDate.toString(),
                    time = selectedTimeSlot!!.startTime,
                    routeUrl = routeUrl
                )

                val appointmentDateTime = LocalDateTime.parse(
                    "${selectedDate}T${selectedTimeSlot!!.startTime}"
                )

                AppointmentReminderScheduler.scheduleOneHourBefore(
                    context = context,
                    appointmentDateTime = appointmentDateTime,
                    doctorName = doctor.name,
                    routeUrl = routeUrl
                )

//                NotificationHelper.showAppointmentBooked(
//                    context = context,
//                    doctorName = doctor.name,
//                    date = selectedDate.toString(),
//                    time = "${selectedTimeSlot!!.startTime}",
//                    route = routeUrl
//                )


                // ✅ Close dialog & reset UI
                showConfirmDialog = false
                selectedTimeSlot = null

                // 🔄 Reload availability
                viewModel.loadAvailability(
                    doctor.id?:"",
                    selectedDate.toString()
                )
            }


//            is ResultState.Success -> {
//                Toast.makeText(
//                    context,
//                    (bookingState as ResultState.Success<String>).data,
//                    Toast.LENGTH_SHORT
//                ).show()
//
//                showConfirmDialog = false
//                selectedTimeSlot = null
//
//                // Reload availability
//                viewModel.loadAvailability(
//                    doctor.id,
//                    selectedDate.toString()
//                )
//            }

            is ResultState.Error -> {
                Toast.makeText(
                    context,
                    (bookingState as ResultState.Error<String>).message,
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }


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
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with Back Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD))
                            .align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1976D2)
                        )
                    }

                    Text(
                        text = "Doctor Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Doctor Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE3F2FD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(50.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = doctor.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFF3E5F5)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = Color(0xFF7B1FA2),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = doctor.speciality,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF7B1FA2)
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Contact Information Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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
                            text = "Contact Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )

                        InfoRow(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = doctor.email,
                            iconColor = Color(0xFF1976D2)
                        )

                        InfoRow(
                            icon = Icons.Default.CurrencyRupee,
                            label = "Consultation Fee",
                            value = "₹${doctor.fee}",
                            iconColor = Color(0xFF7B1FA2)
                        )

                        Divider(color = Color(0xFFE0E0E0))

                        // Map Button
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = patientLat != null && patientLng != null,
                            onClick = {

                                val routeUrl = buildGoogleMapsRouteUrl(
                                    patientLat = patientLat!!,
                                    patientLng = patientLng!!,
                                    doctorLat = doctorLat,
                                    doctorLng = doctorLng
                                )

                                openGoogleMaps(context, routeUrl)


//                                openGoogleMaps(
//                                    context = context,
//                                    patientLat = patientLat!!,
//                                    patientLng = patientLng!!,
//                                    doctorLat = doctorLat,
//                                    doctorLng = doctorLng
//                                )


                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1976D2),
                                disabledContainerColor = Color(0xFFE0E0E0)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (patientLat == null)
                                    "Fetching location..."
                                else
                                    "Show Route on Google Maps",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Date Selection Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Select Date",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF424242)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        DatePickerSection(
                            selectedDate = selectedDate,
                            onDateSelected = {
                                selectedDate = it
                                selectedTimeSlot = null // Reset slot when date changes
                            }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Appointment Booking Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Available Time Slots",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF424242)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Select your preferred appointment time for ${selectedDate}",
                            fontSize = 14.sp,
                            color = Color(0xFF757575)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Time Slots Grid
                        when (availabilityState) {
                            is ResultState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color(0xFF1976D2))
                                }
                            }

                            is ResultState.Error -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EventBusy,
                                            contentDescription = null,
                                            tint = Color(0xFFD32F2F),
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = (availabilityState as ResultState.Error).message,
                                            color = Color(0xFFD32F2F),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            is ResultState.Success -> {
                                val slots = (availabilityState as ResultState.Success<List<SlotDto>>).data

                                if (slots.isEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.EventBusy,
                                                contentDescription = null,
                                                tint = Color(0xFF9E9E9E),
                                                modifier = Modifier.size(48.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "No slots available for this date",
                                                color = Color(0xFF757575),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                } else {
                                    slots.chunked(3).forEach { row ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            row.forEach { slot ->
                                                val timeText = "${slot.startTime} - ${slot.endTime}"

                                                TimeSlotChip(
                                                    time = timeText,
                                                    selectedTimeSlot?.startTime == slot.startTime &&
                                                            selectedTimeSlot?.endTime == slot.endTime,
                                                    isBooked = slot.isBooked,
                                                    onClick = {
                                                        if (!slot.isBooked) {
                                                            selectedTimeSlot = slot
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }

                                            repeat(3 - row.size) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Book Appointment Button
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedTimeSlot != null && selectedTimeSlot?.isBooked == false,
                            onClick = {
                                showConfirmDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7B1FA2),
                                disabledContainerColor = Color(0xFFE0E0E0)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BookOnline,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedTimeSlot != null)
                                    "Book Appointment at ${selectedTimeSlot?.startTime} - ${selectedTimeSlot?.endTime}"
                                else
                                    "Select a time slot",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }

        // Confirmation Dialog
        if (showConfirmDialog && selectedTimeSlot != null) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        text = "Confirm Appointment",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF212121)
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Are you sure you want to book an appointment with:",
                            fontSize = 14.sp,
                            color = Color(0xFF616161)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF1976D2),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = doctor.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF212121)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = Color(0xFF1976D2),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedDate.toString(),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF212121)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = Color(0xFF7B1FA2),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${selectedTimeSlot?.startTime} - ${selectedTimeSlot?.endTime}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF7B1FA2)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CurrencyRupee,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "₹${doctor.fee}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (selectedTimeSlot != null && !doctor.id.isNullOrBlank()) {
                                viewModel.bookAppointment(
                                    doctorId = doctor.id,
                                    date = selectedDate.toString(),
                                    startTime = selectedTimeSlot!!.startTime,
                                    endTime = selectedTimeSlot!!.endTime
                                )
                            }
//                            showConfirmDialog = false
//                            selectedTimeSlot = null
                            // Reload availability to reflect the booking
                            // viewModel.loadAvailability(doctor.id, selectedDate.toString())
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Confirm Booking",
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showConfirmDialog = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color(0xFF757575)
                        )
                    ) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }
    }
}


@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF9E9E9E),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color(0xFF424242),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
fun TimeSlotChip(
    time: String,
    isSelected: Boolean,
    isBooked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isBooked -> Color(0xFFFFEBEE)
        isSelected -> Color(0xFF1976D2)
        else -> Color(0xFFF5F5F5)
    }

    val textColor = when {
        isBooked -> Color(0xFFD32F2F)
        isSelected -> Color.White
        else -> Color(0xFF424242)
    }

    val borderColor = when {
        isBooked -> Color(0xFFEF9A9A)
        isSelected -> null
        else -> Color(0xFFE0E0E0)
    }

    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable(enabled = !isBooked) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = borderColor?.let {
            androidx.compose.foundation.BorderStroke(1.dp, it)
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (isBooked) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = time,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Booked",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = time,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerSection(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val date = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            onDateSelected(date)
        }
    }

    Column {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = Color(0xFF1976D2),
                todayDateBorderColor = Color(0xFF1976D2)
            )
        )
    }
}