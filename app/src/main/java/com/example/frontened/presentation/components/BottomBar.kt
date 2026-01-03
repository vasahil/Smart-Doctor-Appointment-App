package com.example.frontened.presentation.components



import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Person4
import androidx.compose.material.icons.outlined.VideoCall
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.frontened.presentation.navigation.AppRoutes
import com.example.frontened.utils.JwtUtils
import com.example.frontened.utils.TokenManager


@Composable
fun BottomBar(navController: NavController, tokenManager: TokenManager) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem(
                icon = Icons.Outlined.Home,
                selectedIcon = Icons.Filled.Home,
                label = "Home",
                selected = currentRoute == AppRoutes.PatientScreen.route,
                onClick = {
                    navController.navigate(AppRoutes.PatientScreen.route) {
                        popUpTo(AppRoutes.PatientScreen.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )

            val token = tokenManager.getAccessToken()
            val role = token?.let { JwtUtils.getRole(it) }

            BottomBarItem(
                icon = Icons.Outlined.Bookmarks,
                selectedIcon = Icons.Filled.Bookmarks,
                label = "Appointments",
               selected = currentRoute == AppRoutes.MyAppointment.route,
                onClick = {
                    if(role == "PATIENT"){
                        navController.navigate(AppRoutes.MyAppointment.route) {
                            popUpTo(AppRoutes.PatientScreen.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }

                    if(role == "DOCTOR"){
                        navController.navigate(AppRoutes.DoctorAppointmentScreen.route) {
                            popUpTo(AppRoutes.DoctorDashBoard.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }

                }
            )

//            BottomBarItem(
//                icon = Icons.Outlined.VideoCall,
//                selectedIcon = Icons.Filled.Book,
//                label = "VideoCall",
//                selected = currentRoute == AppRoutes.ProfileScreen.route,
//                onClick = {
//                 //   navController.navigate(AppRoutes.BorrowScreen.route)
//                }
//            )

            BottomBarItem(
                icon = Icons.Outlined.Person,
                selectedIcon = Icons.Filled.Person,
                label = "Profile",
                selected = currentRoute == AppRoutes.ProfileScreen.route,
                onClick = {
                    navController.navigate(AppRoutes.ProfileScreen.route)
                }
            )
        }
    }
}

@Composable
fun BottomBarItem(
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "background"
    )

    val iconTint by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconTint"
    )

    val iconSize by animateDpAsState(
        targetValue = if (selected) 26.dp else 24.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconSize"
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp)),
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else icon,
                contentDescription = label,
                modifier = Modifier.size(iconSize),
                tint = iconTint
            )

            if (selected) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = iconTint,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }
        }
    }
}