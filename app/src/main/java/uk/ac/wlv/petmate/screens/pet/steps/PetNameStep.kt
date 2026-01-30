package uk.ac.wlv.petmate.screens.pet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.R
import uk.ac.wlv.petmate.components.ImageTextButton
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.ui.theme.DisplayFontFamily
import uk.ac.wlv.petmate.ui.theme.PrimaryFontFamily
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel

@Composable
fun PetNameStep(viewModel: PetProfileViewModel) {
    val petName by viewModel.petName.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "What's your little one's name?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = PrimaryFontFamily,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            TextField(
                value = petName,
                onValueChange = { viewModel.updatePetName(it) },
                modifier = Modifier
                    .width(200.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = DisplayFontFamily,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.nextStep()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Gray,
                    disabledIndicatorColor = Color.Transparent,

                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            )

        }
        ImageTextButton(
            text = "Next",
            isLoading = false ,
            onClick = { viewModel.nextStep() },
            backgroundColor = MaterialTheme.colorScheme.primary,
            textColor = Color.White,
            progressIndicatorColor = Color.White,
        )

    }
}