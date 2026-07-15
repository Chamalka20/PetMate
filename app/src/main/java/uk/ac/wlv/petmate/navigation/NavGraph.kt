package uk.ac.wlv.petmate.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import org.koin.compose.viewmodel.koinViewModel
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.screens.SignInScreen
import uk.ac.wlv.petmate.screens.SplashScreen
import uk.ac.wlv.petmate.screens.appointment.AppointmentSummaryScreen
import uk.ac.wlv.petmate.screens.pet.PetDetailsScreen
import uk.ac.wlv.petmate.screens.pet.PetEditScreen
import uk.ac.wlv.petmate.screens.pet.PetProfileSetupScreen
import uk.ac.wlv.petmate.screens.vet.NearbyVetsMapScreen
import uk.ac.wlv.petmate.screens.appointment.SelectTimeSlotScreen
import uk.ac.wlv.petmate.screens.vet.AppointmentConfirmationScreen
import uk.ac.wlv.petmate.screens.vet.VetDetailsScreen
import uk.ac.wlv.petmate.screens.vet.VetsListScreen
import uk.ac.wlv.petmate.viewmodel.AppointmentViewModel
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel
import uk.ac.wlv.petmate.viewmodel.SessionViewModel
import uk.ac.wlv.petmate.viewmodel.VetViewModel


@Composable
fun NavGraph(
    sessionViewModel: SessionViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val isLoggedIn by sessionViewModel.isLoggedIn.collectAsState()
    NavHost(
        navController = navController,
        startDestination = "splash",

    ) {

        // Splash Screen
        composable("splash") {
            SplashScreen(
                onFinished = {
                    if (isLoggedIn) {
                        navController.navigate("authenticated") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        navController.navigate("signIn") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            )
        }

        // Sign In Screen
        composable("signIn") {
            SignInScreen(
                navController = navController,

            )
        }

        // Nested navigation for authenticated users
        navigation(
            startDestination = "main",
            route = "authenticated"
        ) {

            composable( route = "main?tab={tab}",
                arguments = listOf(
                    navArgument("tab") {
                        type = NavType.StringType
                        defaultValue = "home"
                    }
                )) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("authenticated")
                }

                val petProfileViewModel: PetProfileViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                val appointmentViewModel: AppointmentViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                val vetViewModel: VetViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                val tab = backStackEntry.arguments?.getString("tab") ?: "home"
                MainScreen(
                    tab = tab,
                    rootNavController = navController,
                    petProfileViewModel = petProfileViewModel,
                    vetViewModel = vetViewModel,
                    appointmentViewModel= appointmentViewModel
                )
            }

            composable(
                route = "petProfileSetup?isDefaultAddBackButton={isDefaultAddBackButton}",
                arguments = listOf(
                    navArgument("isDefaultAddBackButton") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val isDefaultAddBackButton =
                    backStackEntry.arguments?.getBoolean("isDefaultAddBackButton") ?: false
                val parentEntry = remember(backStackEntry) {
                    try {
                        navController.getBackStackEntry("authenticated")
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
                val petProfileViewModel: PetProfileViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry ?: backStackEntry
                )

                PetProfileSetupScreen(
                    navController = navController,
                    isDefaultAddBackButton = isDefaultAddBackButton,
                    viewModel = petProfileViewModel
                )
            }
            composable(
                route = "petDetailsScreen/{petId}",
                arguments = listOf(
                    navArgument("petId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getInt("petId") ?: 0
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("authenticated")
                }
                val petProfileViewModel: PetProfileViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                PetDetailsScreen(
                    petId = petId,
                    petProfileViewModel = petProfileViewModel,
                    navController = navController,
                )
            }
            composable(
                route = "petEditScreen/{petId}",
                arguments = listOf(
                    navArgument("petId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId") ?: ""
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("authenticated")
                }
                val petProfileViewModel: PetProfileViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                PetEditScreen(
                    petId = petId,
                    viewModel = petProfileViewModel,
                    navController = navController
                )
            }

            composable(
                route = "vetsListScreen",

                ) { backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("authenticated")
                }
                val vetViewModel: VetViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                val appointmentViewModel: AppointmentViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                VetsListScreen(
                    navController = navController,
                    vetViewModel = vetViewModel,
                    appointmentViewModel = appointmentViewModel,
                    onBookAppointment = { vet->
                        vetViewModel.loadVet(vet.id)
                        navController.navigate(
                            "selectTimeSlot/${vet.id}"
                        )
                        },
                    onVetClick = {vet->
                        navController.navigate(
                            "vetDetailsScreen/${vet.id}"
                        )
                    }


                    )


            }

            composable(
                route = "nearbyVetsMapScreen",

                ) { backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("authenticated")
                }
                val vetViewModel: VetViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                NearbyVetsMapScreen(
                    vetViewModel = vetViewModel,
                    navController = navController,
                    )
            }


            composable(
                route = "vetDetailsScreen/{vetId}",
                arguments = listOf(
                    navArgument("vetId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val vetId = backStackEntry.arguments?.getInt("vetId") ?:0
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("authenticated")
                }
                val vetViewModel: VetViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                val appointmentViewModel: AppointmentViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                val petProfileViewModel: PetProfileViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                VetDetailsScreen(
                    vetId = vetId,
                    vetViewModel = vetViewModel,
                    navController = navController,
                    appointmentViewModel = appointmentViewModel,
                    petProfileViewModel =petProfileViewModel
                )
            }
            composable(
                route     = "selectTimeSlot/{vetId}/{selectedType}",
                arguments = listOf(
                    navArgument("vetId")        { type = NavType.IntType },
                    navArgument("selectedType") { type = NavType.IntType }
                )

            ) { backStackEntry ->
                val vetId = backStackEntry.arguments?.getInt("vetId") ?:0
                val selectedType = backStackEntry.arguments?.getInt("selectedType") ?: 0
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("authenticated")
                }
                val vetViewModel: VetViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                val appointmentViewModel: AppointmentViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                val petProfileViewModel: PetProfileViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                LaunchedEffect(selectedType) {
                    appointmentViewModel.selectType(selectedType)
                }

                SelectTimeSlotScreen(
                        vetId = vetId,
                        appointmentViewModel = appointmentViewModel,
                        vetViewModel = vetViewModel,
                        onBack               = { navController.popBackStack() },
                        petProfileViewModel =petProfileViewModel,
                        onBookAppointment    = {
                            navController.navigate("appointmentSummary/${vetId}")
                        }
                    )

            }

            composable(
                route     = "appointmentSummary/{vetId}",
                arguments = listOf(
                    navArgument("vetId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val vetId = backStackEntry.arguments?.getInt("vetId") ?: 0

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("authenticated")
                }
                val vetViewModel         : VetViewModel         = koinViewModel(viewModelStoreOwner = parentEntry)
                val appointmentViewModel : AppointmentViewModel = koinViewModel(viewModelStoreOwner = parentEntry)

                val vetState by vetViewModel.selectedVetState.collectAsState()
                val vet = (vetState as? UiState.Success)?.data ?: return@composable

                AppointmentSummaryScreen(
                    vet                  = vet,
                    appointmentViewModel = appointmentViewModel,
                    onBack               = { navController.popBackStack() },
                    rootNavController = navController
                )
            }

            composable("appointmentConfirmation") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("authenticated")
                }
                val appointmentViewModel: AppointmentViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )

                val bookState   by appointmentViewModel.bookState.collectAsState()
                val appointment = (bookState as? UiState.Success)?.data
                    ?: return@composable

                AppointmentConfirmationScreen(
                    appointment = appointment,
                    onViewMyAppointments = {
                        try {
                            Log.d("AppointmentDebug", "View My Appointments clicked")

                            navController.navigate("main?tab=medlog") {
                                popUpTo("authenticated")
                            }
                            Log.d("AppointmentDebug", "Navigation successful")

                            appointmentViewModel.resetBookState()

                            Log.d("AppointmentDebug", "Book state reset")
                        } catch (e: Exception) {
                            Log.e(
                                "AppointmentDebug",
                                "Error while navigating or resetting state",
                                e
                            )
                        }
                    }
                )
            }
        }
    }
}
