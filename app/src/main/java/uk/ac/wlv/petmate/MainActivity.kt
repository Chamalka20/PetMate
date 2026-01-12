package uk.ac.wlv.petmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import uk.ac.wlv.petmate.components.CustomSnackbarHost
import uk.ac.wlv.petmate.navigation.NavGraph
import uk.ac.wlv.petmate.ui.theme.PetMateTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 12+ Splash Screen
        installSplashScreen()

        super.onCreate(savedInstanceState)


        setContent {
            PetMateTheme {
                val context = LocalContext.current
                val gso = remember {
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                }
                val googleSignInClient = remember {
                    GoogleSignIn.getClient(context, gso)
                }
                Box(modifier = Modifier.fillMaxSize()) {

                    NavGraph(googleSignInClient)
                    CustomSnackbarHost()
                }

            }
        }
    }
}

