package uk.ac.wlv.petmate.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.wlv.petmate.components.PetMateBottomBar
import uk.ac.wlv.petmate.screens.CareScreen
import uk.ac.wlv.petmate.screens.HomeScreen
import uk.ac.wlv.petmate.screens.PetsScreen
import uk.ac.wlv.petmate.screens.ProfileScreen

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            PetMateBottomBar(navController)
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {

            composable("home") { HomeScreen() }
            composable("pets") { PetsScreen() }
            composable("care") { CareScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}

