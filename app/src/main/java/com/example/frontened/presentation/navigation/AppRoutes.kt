package com.example.frontened.presentation.navigation

sealed class AppRoutes(val route: String){



    object SignUp : AppRoutes("SignUpScreen")
    object Login : AppRoutes("LoginScreen")
    object PatientScreen: AppRoutes("PatientScreen")
    object DoctorDetailScreen :
        AppRoutes("DoctorDetailScreen/{doctorName}") {

        fun createRoute(doctorName: String): String {
            return "DoctorDetailScreen/$doctorName"
        }
    }


    object ProfileScreen: AppRoutes("ProfileScreen")

    object DoctorDashBoard: AppRoutes("DoctorDashboardScreen")

    object MyAppointment: AppRoutes("MyAppointment")

    object DoctorAppointmentScreen: AppRoutes("DoctorAppointmentScreen")

    object AllDoctorScreen : AppRoutes("AllDoctors")
}