package uk.ac.wlv.petmate.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import uk.ac.wlv.petmate.R
import uk.ac.wlv.petmate.components.ImageTextButton
import uk.ac.wlv.petmate.core.SnackbarController
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.data.model.ApiUser
import uk.ac.wlv.petmate.data.model.Pet
import uk.ac.wlv.petmate.ui.theme.DisplayFontFamily
import uk.ac.wlv.petmate.viewmodel.AuthViewModel
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel


@Composable
fun SignInScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel =  hiltViewModel(),
    petProfileViewModel: PetProfileViewModel =  hiltViewModel(),

    ) {
    val loginState by authViewModel.loginState.collectAsState()
    val petListState by petProfileViewModel.petListState.collectAsState()


    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            petProfileViewModel.loadPetList()
        }
    }
    LaunchedEffect(loginState, petListState) {

        if (loginState !is UiState.Success) return@LaunchedEffect

        when (val pets = petListState) {
            is UiState.Loading -> return@LaunchedEffect

            is UiState.Success -> {
                val destination = if (pets.data.isEmpty()) "petProfileSetup" else "main"
                SnackbarController.showSuccess("Login successful!")
                navController.navigate(destination) {
                    popUpTo("signIn") { inclusive = true }
                }
                authViewModel.resetLoginState()
            }


            else -> return@LaunchedEffect
        }
    }


    SignInScreenContent(
        onGoogleSignInClick = {
            authViewModel.signInWithGoogle()
        },
        loginState = loginState,
        petState =  petListState
    )
}

@Composable
fun SignInScreenContent(
    onGoogleSignInClick: () -> Unit,
    loginState: UiState<ApiUser>,
    petState: UiState<List<Pet>>
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.signin_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Welcome to",
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(color = Color(0xFFFF9800,), fontFamily = DisplayFontFamily,)
                        ) {
                            append("Pet")
                        }
                        withStyle(
                            style = SpanStyle(color = Color.Black, fontFamily = DisplayFontFamily,)
                        ) {
                            append("Mate")
                        }
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )


            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Sign in to continue",
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            ImageTextButton(
                text = "Sign in with Google",
                isLoading = loginState is UiState.Loading || petState is UiState.Loading ,
                imageRes = R.drawable.google_logo,
                onClick = onGoogleSignInClick,
                backgroundColor = MaterialTheme.colorScheme.primary,
                textColor = Color.White,
                progressIndicatorColor = Color.White,
            )


        }

    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
        SignInScreenContent(
            onGoogleSignInClick = {},
            loginState = UiState.Idle,
            petState = UiState.Idle,
        )
}

