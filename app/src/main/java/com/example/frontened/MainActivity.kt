package com.example.frontened

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.frontened.presentation.Auth.AuthState
import com.example.frontened.presentation.Auth.AuthViewModel
import com.example.frontened.presentation.navigation.AppRoutes
import com.example.frontened.presentation.navigation.MainNavigation
import com.example.frontened.ui.theme.FrontenedTheme
import com.example.frontened.utils.JwtUtils
import com.example.frontened.utils.LocationProvider
import com.example.frontened.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var locationProvider: LocationProvider

    @Inject
    lateinit var tokenManager: TokenManager
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val authViewModel: AuthViewModel = hiltViewModel()
            val authState by authViewModel.authState.collectAsState()

            val token = tokenManager.getAccessToken()
            val role = token?.let{ JwtUtils.getRole(token) }

            when (authState) {
                AuthState.Authenticated -> {
                    if(role == "PATIENT"){
                        MainNavigation(startScreen = AppRoutes.PatientScreen.route, locationProvider, tokenManager)
                    }

                    if(role == "DOCTOR") {
                        MainNavigation(startScreen = AppRoutes.DoctorDashBoard.route, locationProvider, tokenManager)
                    }

                }

                AuthState.Unauthenticated -> {
                    MainNavigation(startScreen = AppRoutes.Login.route, locationProvider, tokenManager)
                }
            }

           // MainNavigation(AppRoutes.Login.route, locationProvider, tokenManager)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}

