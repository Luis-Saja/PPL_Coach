package com.ukdw.pplaicoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ukdw.pplaicoach.navigation.PPLNavGraph
import com.ukdw.pplaicoach.navigation.Screen
import com.ukdw.pplaicoach.ui.theme.PPLAICoachTheme

/**
 * === MAIN ACTIVITY ===
 * Entry point aplikasi PPL AI Coach.
 * Menggunakan single-activity architecture dengan Jetpack Navigation Compose.
 * Splash screen ditampilkan saat aplikasi pertama kali dibuka.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Pasang splash screen sebelum super.onCreate
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PPLAICoachTheme {
                PPLAICoachMainScreen()
            }
        }
    }
}

/**
 * Data class untuk item navigasi bottom bar.
 */
data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * Komposisi utama aplikasi dengan bottom navigation bar.
 * Bottom bar memiliki 5 tab: Home, Latihan (AI), Tracker, Riwayat, Profil
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PPLAICoachMainScreen() {
    val navController = rememberNavController()

    // Daftar item bottom navigation (5 tab)
    val bottomNavItems = listOf(
        BottomNavItem(
            label = "Home",
            route = Screen.Home.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            label = "AI Coach",
            route = Screen.Input.route,
            selectedIcon = Icons.Filled.FitnessCenter,
            unselectedIcon = Icons.Outlined.FitnessCenter
        ),
        BottomNavItem(
            label = "Tracker",
            route = Screen.Tracker.route,
            selectedIcon = Icons.Filled.EditNote,
            unselectedIcon = Icons.Outlined.EditNote
        ),
        BottomNavItem(
            label = "Riwayat",
            route = Screen.History.route,
            selectedIcon = Icons.Filled.History,
            unselectedIcon = Icons.Outlined.History
        ),
        BottomNavItem(
            label = "Profil",
            route = Screen.Profile.route,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    // Rute yang menampilkan bottom bar
    val bottomBarRoutes = bottomNavItems.map { it.route }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Sembunyikan bottom bar di halaman result, test mode, dan progress
    val showBottomBar = currentDestination?.route in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    // Hindari menumpuk back stack
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Hindari duplikat destinasi
                                    launchSingleTop = true
                                    // Restore state saat kembali ke tab
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        PPLNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
