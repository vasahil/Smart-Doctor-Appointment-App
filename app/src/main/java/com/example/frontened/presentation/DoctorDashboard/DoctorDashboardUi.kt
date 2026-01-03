package com.example.frontened.presentation.DoctorDashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontened.data.dto.SlotDto
import com.example.frontened.utils.JwtUtils
import com.example.frontened.utils.TokenManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboardScreen(
    navController: NavController,
//    doctorId: String, // Pass this from your auth/session
    tokenManager: TokenManager,
    viewModel: DoctorDashboardViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var date by remember { mutableStateOf("") }

    // Healthcare color scheme
    val primaryHealthcare = Color(0xFF00897B) // Teal
    val availableColor = Color(0xFF66BB6A) // Green for available
    val bookedColor = Color(0xFFEF5350) // Red for booked

    val allSlots = remember { generateSlots() }
    var selectedSlots by remember { mutableStateOf(setOf<SlotDto>()) }

    val state by viewModel.state.collectAsState()

    // Merge booked slots with all slots
    val mergedSlots = remember(allSlots, state.bookedSlots) {
        allSlots.map { slot ->
            val bookedSlot = state.bookedSlots.find {
                it.startTime == slot.startTime && it.endTime == slot.endTime
            }
            if (bookedSlot != null) {
                slot.copy(isBooked = bookedSlot.isBooked)
            } else {
                slot
            }
        }
    }

    val token = tokenManager.getAccessToken()
    val doctorId = token?.let { JwtUtils.getRole(it) }

    // Load availability when date changes
    LaunchedEffect(date) {
        if (date.isNotBlank() && date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            viewModel.loadAvailability(doctorId!!, date)
        }
    }

    // Show success message and clear state
    LaunchedEffect(state.message) {
        state.message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
            selectedSlots = setOf()
            // Reload availability after saving
            if (date.isNotBlank()) {
                viewModel.loadAvailability(doctorId!!, date)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Doctor Availability",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryHealthcare,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .padding(bottom = 72.dp)
                .background(Color(0xFFF5F5F5))
        ) {
            // Date Selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = primaryHealthcare,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select Date",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF333333)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Refresh button
                        if (date.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    viewModel.loadAvailability(doctorId!!, date)
                                },
                                enabled = !state.loadingSlots
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = primaryHealthcare
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        placeholder = { Text("2025-01-15") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryHealthcare,
                            focusedLabelColor = primaryHealthcare
                        ),
                        singleLine = true
                    )

                    if (state.loadingSlots) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = primaryHealthcare
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Loading availability...",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // Time Slots Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Available Time Slots",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Text(
                        text = "Select the time slots you're available",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Legend
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        LegendItem(
                            color = availableColor,
                            label = "Selected"
                        )
                        LegendItem(
                            color = Color.LightGray,
                            label = "Available"
                        )
                        LegendItem(
                            color = bookedColor,
                            label = "Booked"
                        )
                    }

                    Divider(modifier = Modifier.padding(bottom = 12.dp))

                    if (state.slotsError != null) {
                        Text(
                            text = state.slotsError!!,
                            color = Color(0xFFFF6B6B),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        items(mergedSlots) { slot ->
                            val isSelected = selectedSlots.contains(slot)
                            val isBooked = slot.isBooked

                            SlotCard(
                                slot = slot,
                                isSelected = isSelected,
                                isBooked = isBooked,
                                primaryColor = primaryHealthcare,
                                availableColor = availableColor,
                                bookedColor = bookedColor,
                                onClick = {
                                    if (!isBooked) {
                                        selectedSlots = if (isSelected) {
                                            selectedSlots - slot
                                        } else {
                                            selectedSlots + slot
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Save Button and Status
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = date.isNotBlank() && selectedSlots.isNotEmpty() && !state.loading,
                    onClick = {
                        viewModel.addAvailability(
                            date = date,
                            slots = selectedSlots.toList()
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryHealthcare,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Availability",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Error message
                state.error?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFC62828),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SlotCard(
    slot: SlotDto,
    isSelected: Boolean,
    isBooked: Boolean,
    primaryColor: Color,
    availableColor: Color,
    bookedColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { if (!isBooked) onClick() },
        enabled = !isBooked,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isBooked -> bookedColor.copy(alpha = 0.1f)
                isSelected -> availableColor.copy(alpha = 0.15f)
                else -> Color(0xFFFAFAFA)
            },
            disabledContainerColor = bookedColor.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, availableColor)
        } else if (isBooked) {
            androidx.compose.foundation.BorderStroke(1.dp, bookedColor.copy(alpha = 0.3f))
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${slot.startTime} - ${slot.endTime}",
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = when {
                    isBooked -> bookedColor
                    isSelected -> availableColor
                    else -> Color(0xFF333333)
                }
            )

            when {
                isBooked -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = bookedColor
                    ) {
                        Text(
                            text = "Booked",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
                isSelected -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = availableColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            modifier = Modifier.size(12.dp),
            color = color,
            shape = RoundedCornerShape(2.dp)
        ) {}
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

fun generateSlots(): List<SlotDto> {
    val slots = mutableListOf<SlotDto>()
    var hour = 9
    var minute = 0

    repeat(16) {
        val start = "%02d:%02d".format(hour, minute)

        minute += 30
        if (minute == 60) {
            hour++
            minute = 0
        }

        val end = "%02d:%02d".format(hour, minute)

        slots.add(
            SlotDto(
                startTime = start,
                endTime = end,
                isBooked = false
            )
        )
    }
    return slots
}