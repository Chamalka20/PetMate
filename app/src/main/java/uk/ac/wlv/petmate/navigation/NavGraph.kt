package uk.ac.wlv.petmate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import uk.ac.wlv.petmate.screens.HomeScreen
import uk.ac.wlv.petmate.screens.SignInScreen
import uk.ac.wlv.petmate.screens.SplashScreen


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
                    navController.navigate("signIn") {
                        popUpTo("splash") { inclusive = true }
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

        // 🏠 Main (Bottom Navigation)
        composable("main") {
            MainScreen()
        }
    }
}
