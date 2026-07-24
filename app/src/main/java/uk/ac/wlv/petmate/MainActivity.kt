package uk.ac.wlv.petmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.wlv.petmate.components.CustomSnackbarHost
import uk.ac.wlv.petmate.navigation.NavGraph
import uk.ac.wlv.petmate.ui.theme.PetMateTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PetMateTheme(dynamicColor = false ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    NavGraph()
                    CustomSnackbarHost()
                }

            }
        }
    }
}

