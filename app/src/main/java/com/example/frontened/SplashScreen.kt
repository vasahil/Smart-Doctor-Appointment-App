//package com.example.frontened
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.painterResource
//import androidx.navigation.NavController
//import com.example.frontened.presentation.navigation.AppRoutes
//import com.example.frontened.utils.TokenManager
//import kotlinx.coroutines.delay
//
//@Composable
//fun SplashScreen(
//    navController: NavController,
//    tokenManager: TokenManager
//) {
//    LaunchedEffect(Unit) {
//        delay(1000) // 1 second splash
//
//        val destination = if (tokenManager.getAccessToken().isNullOrEmpty()) {
//            AppRoutes.Login.route
//        } else {
//            AppRoutes.PatientScreen.route
//        }
//
//        navController.navigate(destination) {
//            popUpTo(AppRoutes.SplashScreen.route) { inclusive = true }
//        }
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.img),
//            contentDescription = "App Logo",
//            modifier = Modifier.fillMaxSize()
//        )
//    }
//}
