package uk.ac.wlv.petmate.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uk.ac.wlv.petmate.components.PetMateBottomBar
import uk.ac.wlv.petmate.screens.mainScreens.CareScreen
import uk.ac.wlv.petmate.screens.mainScreens.home.HomeScreen
import uk.ac.wlv.petmate.screens.mainScreens.ProfileScreen
import androidx.navigation.compose.rememberNavController
import uk.ac.wlv.petmate.screens.mainScreens.EmergencyScreen
import uk.ac.wlv.petmate.screens.mainScreens.medlog.MedLogScreen
import uk.ac.wlv.petmate.viewmodel.AppointmentViewModel
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel
import uk.ac.wlv.petmate.viewmodel.PrescriptionViewModel
import uk.ac.wlv.petmate.viewmodel.VetViewModel

@Composable
fun MainScreen(  tab: String?,rootNavController: NavController,petProfileViewModel: PetProfileViewModel,vetViewModel: VetViewModel,appointmentViewModel: AppointmentViewModel,prescriptionViewModel: PrescriptionViewModel) {
    val bottomNavController = rememberNavController()
    val startDestination =
        if(tab == "medlog") {
            "medlog"
        } else {
            "home"
        }
    Scaffold(
        bottomBar = { PetMateBottomBar(bottomNavController) }

    ) { paddingValues ->

        NavHost(
            navController = bottomNavController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen(rootNavController = rootNavController, petProfileViewModel = petProfileViewModel, vetViewModel =vetViewModel ) }
            composable("medlog") {
                MedLogScreen(
                    appointmentViewModel = appointmentViewModel,
                    prescriptionViewModel =prescriptionViewModel,
                    rootNavController = rootNavController
                )
            }
            composable("emergency") { EmergencyScreen() }
            composable("mating") { CareScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}

