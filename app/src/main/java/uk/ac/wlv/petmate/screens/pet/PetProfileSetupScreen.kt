package uk.ac.wlv.petmate.screens.pet

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import uk.ac.wlv.petmate.components.CustomSnackbarHost
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.screens.pet.steps.PetAgeStep
import uk.ac.wlv.petmate.screens.pet.steps.PetImageStep
import uk.ac.wlv.petmate.viewmodel.AuthViewModel
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetProfileSetupScreen(
    navController: NavHostController,
    viewModel: PetProfileViewModel = koinViewModel(),

) {

    val currentStep by viewModel.currentStep.collectAsState()
    val savePetState by viewModel.savePetState.collectAsState()

    LaunchedEffect(savePetState) {
        if (savePetState is UiState.Success) {
            navController.navigate("main") {
                popUpTo("petSetup") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Setup") },
                navigationIcon = {
                    if (currentStep > 0) {
                        IconButton(onClick = { viewModel.previousStep() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                actions = {
                    if (currentStep < 5) {
                        TextButton(onClick = { viewModel.skipStep() }) {
                            Text("Skip")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Progress Indicator
                LinearProgressIndicator(
                progress = { (currentStep + 1) / 6f },
                modifier = Modifier.fillMaxWidth(),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )

                // Step Content
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        slideInHorizontally(
                                                initialOffsetX = { it },
                                                animationSpec = tween(300)
                                            ).togetherWith(
                            slideOutHorizontally(
                                                targetOffsetX = { -it },
                                                animationSpec = tween(300)
                                            )
                        )
                    },
                    label = "step_transition"
                ) { step ->
                    when (step) {
                        0 -> PetNameStep(viewModel)
                        1 -> PetAgeStep(viewModel)
                        2 -> PetImageStep(viewModel)
                        3 -> SpayedNeuteredStep(viewModel)
                        4 -> MedicalConditionsStep(viewModel)
                        5 -> PetTypeStep(viewModel)

                    }
                }
            }


        }
    }
}