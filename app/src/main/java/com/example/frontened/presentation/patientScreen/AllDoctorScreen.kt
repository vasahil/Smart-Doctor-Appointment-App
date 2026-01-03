package com.example.frontened.presentation.patientScreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontened.common.ResultState
import com.example.frontened.data.dto.DoctorDto
import com.example.frontened.presentation.Auth.AuthViewModel
import com.example.frontened.presentation.navigation.AppRoutes
import com.example.frontened.utils.LocationProvider
import com.example.frontened.utils.RequestLocationPermission
import com.example.frontened.utils.RequireGpsEnabled

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllDoctorScreen(
    navController: NavController,
    viewModel: PatientViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    locationProvider: LocationProvider
) {
    var hasPermission by remember { mutableStateOf(false) }
    var locationFetched by remember { mutableStateOf(false) }
    var gpsEnabled by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var sortByDistance by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }

    // Request Location Permission
    if (!hasPermission) {
        RequestLocationPermission {
            hasPermission = true
        }
    }

    // Require GPS
    if (hasPermission && !gpsEnabled) {
        RequireGpsEnabled {
            gpsEnabled = true
        }
    }

    // Load nearby doctors
    LaunchedEffect(hasPermission, gpsEnabled) {
        if (hasPermission && gpsEnabled && !locationFetched) {
            locationFetched = true
            locationProvider.getLastLocation { lat, lng ->
                Log.d("ALL_DOCTORS", "Location: $lat, $lng")
                viewModel.loadNearbyDoctors(lat, lng, 1000)
            }
        }
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "All Doctors",
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
                    containerColor = Color(0xFF1976D2),
                    titleContentColor = Color.White
                ),
                actions = {
                    // Filter Button
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Search and Sort Section
            SearchAndSortSection(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                sortByDistance = sortByDistance,
                onSortToggle = { sortByDistance = !sortByDistance }
            )

            // Content based on state
            when (val result = state) {
                is ResultState.Loading -> {
                    LoadingState()
                }

                is ResultState.Error -> {
                    ErrorState(error = result.message)
                }

                is ResultState.Success -> {
                    val filteredDoctors = if (searchQuery.isBlank()) {
                        result.data
                    } else {
                        result.data.filter { doctor ->
                            doctor.name.contains(searchQuery, ignoreCase = true) ||
                                    doctor.speciality.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    val finalDoctors = if (sortByDistance) {
                        filteredDoctors.sortedBy { it.distance }
                    } else {
                        filteredDoctors
                    }

                    DoctorsList(
                        doctors = finalDoctors,
                        navController = navController
                    )
                }
            }
        }
    }

    // Filter Dialog
    if (showFilterDialog) {
        FilterDialog(
            sortByDistance = sortByDistance,
            onSortChange = { sortByDistance = it },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@Composable
fun SearchAndSortSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    sortByDistance: Boolean,
    onSortToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Sort Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (sortByDistance) Color(0xFF1976D2) else Color(0xFFE3F2FD),
                modifier = Modifier.clickable(onClick = onSortToggle)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Sort by distance",
                        tint = if (sortByDistance) Color.White else Color(0xFF1976D2),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (sortByDistance) "Sorted by Distance" else "Sort by Distance",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (sortByDistance) Color.White else Color(0xFF1976D2)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Search doctors or speciality...",
                    color = Color(0xFF9E9E9E)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFF1976D2)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color(0xFF757575)
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1976D2),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

@Composable
fun DoctorsList(
    doctors: List<DoctorDto>,
    navController: NavController
) {
    if (doctors.isEmpty()) {
        EmptyDoctorsState()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Available Doctors",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFE3F2FD)
                    ) {
                        Text(
                            text = "${doctors.size} found",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1976D2),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Doctors List
            items(doctors) { doctor ->
                DoctorCard(
                    doctor = doctor,
                    onClick = {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("doctor", doctor)

                        navController.navigate(
                            AppRoutes.DoctorDetailScreen.createRoute(doctor.name)
                        )
                    }
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun DoctorCard(
    doctor: DoctorDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Doctor Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Doctor Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = doctor.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color(0xFF7B1FA2),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = doctor.speciality,
                        fontSize = 14.sp,
                        color = Color(0xFF757575)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fee and Distance
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Fee Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF3E5F5)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyRupee,
                                contentDescription = null,
                                tint = Color(0xFF7B1FA2),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${doctor.fee}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF7B1FA2)
                            )
                        }
                    }

                    // Distance Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE3F2FD)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = String.format("%.1f km", doctor.distance),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF9E9E9E),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color(0xFF1976D2),
                strokeWidth = 3.dp
            )
            Text(
                text = "Finding nearby doctors...",
                color = Color(0xFF616161),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ErrorState(error: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFEBEE)
            ),
            modifier = Modifier.padding(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = Color(0xFFD32F2F),
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Error Loading Doctors",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFC62828)
                )
                Text(
                    text = error,
                    color = Color(0xFF424242),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun EmptyDoctorsState() {
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
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Text(
                text = "No Doctors Found",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF212121)
            )
            Text(
                text = "Try adjusting your search or filters",
                fontSize = 14.sp,
                color = Color(0xFF757575),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FilterDialog(
    sortByDistance: Boolean,
    onSortChange: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = Color(0xFF1976D2)
            )
        },
        title = {
            Text(
                "Sort & Filter Options",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = null,
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Sort by Distance",
                            fontSize = 15.sp
                        )
                    }
                    Switch(
                        checked = sortByDistance,
                        onCheckedChange = onSortChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF1976D2)
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF1976D2)
                )
            ) {
                Text("Done", fontWeight = FontWeight.SemiBold)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}