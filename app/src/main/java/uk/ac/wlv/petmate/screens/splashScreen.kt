package uk.ac.wlv.petmate.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import uk.ac.wlv.petmate.R
import uk.ac.wlv.petmate.ui.theme.DisplayFontFamily

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(true) {
        delay(2000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(R.drawable.pet_logo),
                contentDescription = "PetMate Logo",
                modifier = Modifier.size(80.dp)
            )
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFFFF9800),
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    ) {
                        append("Pet")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.Black,
                            fontFamily = DisplayFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                    ) {
                        append("Mate")
                    }
                }
            )

        }

    }
}