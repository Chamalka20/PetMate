package uk.ac.wlv.petmate.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import org.koin.androidx.compose.koinViewModel
import uk.ac.wlv.petmate.R
import uk.ac.wlv.petmate.components.ImageTextButton
import uk.ac.wlv.petmate.core.SnackbarController
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.model.User
import uk.ac.wlv.petmate.viewmodel.AuthViewModel


@Composable
fun SignInScreen(
    navController: NavHostController,
    googleSignInClient: GoogleSignInClient,
    viewModel: AuthViewModel = koinViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        viewModel.signIn(task)
    }

    // Handle login state changes
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is UiState.Success<User> -> {
                SnackbarController.showSuccess("Login successful!")
                navController.navigate("main") {
                    popUpTo("signIn") { inclusive = true }
                }

                viewModel.resetLoginState()
            }

            is UiState.Error -> {
                SnackbarController.showError(state.message)

            }

            is UiState.Loading -> {
                // Loading state handled in UI
            }

            is UiState.Idle -> {
                // Idle state
            }
        }
    }

    // Observe errors from BaseViewModel (for additional error handling)
    LaunchedEffect(Unit) {
        viewModel.error.collect { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    SignInScreenContent(
        onGoogleSignInClick = {
            launcher.launch(googleSignInClient.signInIntent)
        },
        loginState = loginState,
    )
}

@Composable
fun SignInScreenContent(
    onGoogleSignInClick: () -> Unit,
    loginState: UiState<User>,
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.signin_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
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
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center){
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(color = Color(0xFFFF9800))
                        ) {
                            append("Pet")
                        }
                        withStyle(
                            style = SpanStyle(color = Color.White)
                        ) {
                            append("Mate")
                        }
                    },
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(1.dp))
                Image(
                    painter = painterResource(id =R.drawable.dog_foot ),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp)
                )


            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Sign in to continue",
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            ImageTextButton(
                text = "Sign in with Google",
                isLoading = loginState is UiState.Loading ,
                imageRes = R.drawable.google_logo,
                onClick = onGoogleSignInClick
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
        )
}

