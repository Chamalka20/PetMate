package uk.ac.wlv.petmate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import uk.ac.wlv.petmate.screens.HomeScreen
import uk.ac.wlv.petmate.screens.SignInScreen
import uk.ac.wlv.petmate.screens.SplashScreen


@Composable
fun PetMateNavHost(  googleSignInClient: GoogleSignInClient) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen {
                navController.navigate("signIn") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
        composable("signIn") {
            SignInScreen(
                navController,
               googleSignInClient = googleSignInClient,

            )
        }
        composable("home") {
            HomeScreen()
        }

    }
}