package uk.ac.wlv.petmate.navigation

import android.R.attr.type
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavArgument
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import org.koin.compose.viewmodel.koinViewModel
import uk.ac.wlv.petmate.screens.SignInScreen
import uk.ac.wlv.petmate.screens.SplashScreen
import uk.ac.wlv.petmate.screens.pet.PetDetailsScreen
import uk.ac.wlv.petmate.screens.pet.PetEditScreen
import uk.ac.wlv.petmate.screens.pet.PetProfileSetupScreen
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel


@Composable
fun NavGraph(
    googleSignInClient: GoogleSignInClient
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash",
    ) {

        // Splash Screen
        composable("splash") {
            SplashScreen(
                onFinished = {
                    if(FirebaseAuth.getInstance().currentUser != null) {
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
                googleSignInClient = googleSignInClient
            )
        }

        // Nested navigation for authenticated users
        navigation(
            startDestination = "main",
            route = "authenticated"
        ) {
            composable("main") { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("authenticated")
                }

                val petProfileViewModel: PetProfileViewModel = koinViewModel(
                    viewModelStoreOwner = parentEntry
                )
                MainScreen(
                    rootNavController = navController,
                    petProfileViewModel = petProfileViewModel
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
        }
    }
}
