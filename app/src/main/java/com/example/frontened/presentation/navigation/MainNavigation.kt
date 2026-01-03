package com.example.frontened.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.PathHitTester
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.frontened.presentation.DoctorDashboard.DoctorDashboardScreen
import com.example.frontened.presentation.ProfileScreen.ProfileScreen

import com.example.frontened.presentation.SignupScreen.SignUpScreen
import com.example.frontened.presentation.components.BottomBar
import com.example.frontened.presentation.DoctorAppointments.DoctorAppointmentsScreen
import com.example.frontened.presentation.loginScreen.LoginScreen
import com.example.frontened.presentation.patientScreen.AllDoctorScreen


import com.example.frontened.presentation.patientScreen.DoctorDetailScreen
import com.example.frontened.presentation.patientScreen.PatientAppointmentScreen
import com.example.frontened.presentation.patientScreen.patientScreen
import com.example.frontened.utils.LocationProvider
import com.example.frontened.utils.TokenManager


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavigation(startScreen: String, locationProvider: LocationProvider, tokenManager: TokenManager){
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoutes = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoutes?.substringBefore("/") in listOf(
        AppRoutes.PatientScreen.route,
        AppRoutes.DoctorDetailScreen.route,
        AppRoutes.MyAppointment.route,
        AppRoutes.ProfileScreen.route,
        AppRoutes.DoctorDashBoard.route,
        AppRoutes.AllDoctorScreen.route
    )
    val context = LocalContext.current


    Scaffold(
        bottomBar = {
            if(showBottomBar){
                BottomBar(navController, tokenManager)
            }
        }
    )
    {innerPadding->



        NavHost(navController, startDestination = startScreen){

//            composable(AppRoutes.SplashScreen.route) {
//                SplashScreen(navController, tokenManager)
//            }

            composable(AppRoutes.SignUp.route){
                SignUpScreen(navController=navController, tokenManager = tokenManager)
            }

            composable(AppRoutes.Login.route){
                LoginScreen(navController, tokenManager = tokenManager)
            }

            composable(AppRoutes.PatientScreen.route){

                patientScreen(navController, locationProvider = locationProvider)
            }

            composable(
                route = AppRoutes.DoctorDetailScreen.route,
                arguments = listOf(
                    navArgument("doctorName") { type = NavType.StringType }
                )
            ) {
                DoctorDetailScreen(
                    navController = navController,
                    locationProvider = locationProvider
                )
            }

            composable(AppRoutes.ProfileScreen.route) {
                ProfileScreen(navController)
            }

            composable(AppRoutes.DoctorDashBoard.route) {
                DoctorDashboardScreen(navController = navController, tokenManager = tokenManager)
            }

            composable(AppRoutes.MyAppointment.route) {
                PatientAppointmentScreen(navController)
            }

            composable(AppRoutes.DoctorAppointmentScreen.route){
                DoctorAppointmentsScreen(navController)
            }
            composable(AppRoutes.AllDoctorScreen.route) {
                AllDoctorScreen(navController, locationProvider = locationProvider)
            }
        }

    }
}